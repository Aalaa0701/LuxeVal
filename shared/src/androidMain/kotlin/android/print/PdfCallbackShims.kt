package android.print

/**
 * Workaround for a known Android SDK stub-jar issue (compileSdk 34+): the constructors
 * of PrintDocumentAdapter.LayoutResultCallback / WriteResultCallback are marked
 * package-private in the compile-time stub jar, blocking direct subclassing from
 * app code — even though the real runtime classes are public. Declaring these thin
 * subclasses inside the `android.print` package gives us JVM-level access to the
 * package-private constructor, producing ordinary public classes we can subclass
 * freely elsewhere.
 */
abstract class PublicLayoutResultCallback : PrintDocumentAdapter.LayoutResultCallback()

abstract class PublicWriteResultCallback : PrintDocumentAdapter.WriteResultCallback()