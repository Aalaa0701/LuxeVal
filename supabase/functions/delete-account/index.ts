import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const SUPABASE_URL = Deno.env.get("SUPABASE_URL")!;
const SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;

Deno.serve(async (req) => {
  if (req.method !== "POST") {
    return new Response("Method not allowed", { status: 405 });
  }

  const authHeader = req.headers.get("Authorization");
  if (!authHeader?.startsWith("Bearer ")) {
    return new Response(JSON.stringify({ error: "Missing auth token" }), { status: 401 });
  }
  const accessToken = authHeader.replace("Bearer ", "");

  // Verify the caller's own token — this is what stops user A from deleting user B.
  // Using the anon key here (not service role) so this call is subject to normal auth checks.
  const callerClient = createClient(SUPABASE_URL, Deno.env.get("SUPABASE_ANON_KEY")!, {
    global: { headers: { Authorization: `Bearer ${accessToken}` } },
  });
  const { data: userData, error: userError } = await callerClient.auth.getUser();
  if (userError || !userData?.user) {
    return new Response(JSON.stringify({ error: "Invalid session" }), { status: 401 });
  }
  const userId = userData.user.id;

  // Now use the service role client for the actual destructive work.
  const adminClient = createClient(SUPABASE_URL, SERVICE_ROLE_KEY);

  // 1. Clean up Storage first — buckets are keyed "{userId}/..." per your
  //    PhotoRepository convention, so listing by that prefix finds everything.
  for (const bucket of ["item-photos", "business-assets"]) {
    const { data: files, error: listError } = await adminClient.storage
      .from(bucket)
      .list(userId, { limit: 1000 });

    if (!listError && files && files.length > 0) {
      // list() only returns top-level entries under the prefix; item-photos
      // is nested one level deeper (userId/itemId/file.jpg), so recurse once.
      const paths: string[] = [];
      for (const entry of files) {
        if (entry.id === null) {
          // it's a "directory" (itemId folder) — list inside it
          const { data: nested } = await adminClient.storage
            .from(bucket)
            .list(`${userId}/${entry.name}`, { limit: 1000 });
          nested?.forEach((f) => paths.push(`${userId}/${entry.name}/${f.name}`));
        } else {
          paths.push(`${userId}/${entry.name}`);
        }
      }
      if (paths.length > 0) {
        await adminClient.storage.from(bucket).remove(paths);
      }
    }
  }

  // 2. Delete the auth user — cascades through clients/appraisals/items/
  //    business_profiles via the FKs added in the migration above.
  const { error: deleteError } = await adminClient.auth.admin.deleteUser(userId);
  if (deleteError) {
    return new Response(JSON.stringify({ error: deleteError.message }), { status: 500 });
  }

  return new Response(JSON.stringify({ success: true }), {
    headers: { "Content-Type": "application/json" },
  });
});