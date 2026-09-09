package com.example.luxeval.presentation.auth


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.luxeval.config.LuxValConfig
import com.example.luxeval.presentation.theme.LuxeShapes
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// Gold gradient lifted straight from the logo's own palette, so the button
// reads as the same brand gold in both light and dark mode rather than
// shifting with MaterialTheme.colorScheme.secondary.
private val GoldGradient = Brush.horizontalGradient(
    listOf(Color(0xFFB89232), Color(0xFFF0D06A), Color(0xFFB89232))
)

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onNavigateToResetPassword: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    var dialogEvent by remember { mutableStateOf<AuthUiEvent?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event -> dialogEvent = event }
    }

    Scaffold { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).padding(LuxeSpacing.md),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = LuxeShapes.lg,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            ) {
                Column(
                    Modifier.padding(LuxeSpacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier.size(64.dp).clip(CircleShape).background(
//                            GoldGradient
                            MaterialTheme.colorScheme.primary
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_other_jewelry),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.height(LuxeSpacing.sm))
                    Text(stringResource(Res.string.app_name), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(
                        stringResource(Res.string.auth_tagline),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(LuxeSpacing.lg))
                    AuthTabToggle(current = formState.tab, onSelected = viewModel::onTabChanged)
                    Spacer(Modifier.height(LuxeSpacing.lg))

                    if (formState.tab == AuthTab.SIGN_UP) {
                        LabeledField(
                            label = stringResource(Res.string.auth_full_name),
                            value = formState.fullName,
                            onValueChange = viewModel::onFullNameChanged,
                            placeholder = "Sophia Hartwell",
                            leadingIcon = Res.drawable.ic_person
                        )
                        Spacer(Modifier.height(LuxeSpacing.sm))
                    }

                    LabeledField(
                        label = stringResource(Res.string.auth_email_address),
                        value = formState.email,
                        onValueChange = viewModel::onEmailChanged,
                        placeholder = stringResource(Res.string.auth_email_placeholder),
                        leadingIcon = Res.drawable.ic_mail
                    )
                    Spacer(Modifier.height(LuxeSpacing.sm))

                    LabeledField(
                        label = stringResource(Res.string.auth_password),
                        value = formState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        placeholder = stringResource(Res.string.auth_password_placeholder),
                        leadingIcon = Res.drawable.ic_lock,
                        isPassword = true,
                        isVisible = formState.isPasswordVisible,
                        onToggleVisibility = viewModel::togglePasswordVisibility
                    )
                    if (formState.tab == AuthTab.SIGN_UP) {
                        Spacer(Modifier.height(LuxeSpacing.xs))
                        Text(
                            stringResource(Res.string.auth_password_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(Modifier.height(LuxeSpacing.sm))
                        LabeledField(
                            label = stringResource(Res.string.auth_confirm_password),
                            value = formState.confirmPassword,
                            onValueChange = viewModel::onConfirmPasswordChanged,
                            placeholder = stringResource(Res.string.auth_password_placeholder),
                            leadingIcon = Res.drawable.ic_lock,
                            isPassword = true,
                            isVisible = formState.isConfirmPasswordVisible,
                            onToggleVisibility = viewModel::toggleConfirmPasswordVisibility
                        )
                    }

                    Spacer(Modifier.height(LuxeSpacing.sm))

                    if (formState.tab == AuthTab.LOGIN) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = formState.rememberMe, onCheckedChange = viewModel::onRememberMeChanged)
                                Text(stringResource(Res.string.auth_remember_me), style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = onNavigateToResetPassword) {
                                Text(stringResource(Res.string.auth_forgot_password), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    } else {
                        val uriHandler = LocalUriHandler.current
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = formState.agreedToTerms, onCheckedChange = viewModel::onAgreedToTermsChanged)
                            val annotatedText = buildAnnotatedString {
                                append(stringResource(Res.string.auth_agree))
                                pushStringAnnotation(tag = "tos", annotation = LuxValConfig.TERMS_OF_SERVICE_URL)
                                withStyle(
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.secondary,
                                        textDecoration = TextDecoration.Underline
                                    )
                                ) {
                                    append(stringResource(Res.string.auth_terms))
                                }
                                pop()
                                append(stringResource(Res.string.auth_and))
                                pushStringAnnotation(tag = "privacy", annotation = LuxValConfig.PRIVACY_POLICY_URL)
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary, textDecoration = TextDecoration.Underline)) {
                                    append(stringResource(Res.string.auth_privacy))
                                }
                                pop()
                            }
                            ClickableText(
                                text = annotatedText,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                                onClick = { offset ->
                                    annotatedText.getStringAnnotations(start = offset, end = offset).firstOrNull()?.let {
                                        uriHandler.openUri(it.item)
                                    }
                                }
                            )
                        }
                    }

                    formState.fieldError?.let {
                        Spacer(Modifier.height(LuxeSpacing.xs))
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(LuxeSpacing.md))

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(LuxeShapes.full)
                            .background(
//                                GoldGradient
                                MaterialTheme.colorScheme.primary
                            )
                            .let { base ->
                                if (formState.isSubmitting) base else base // clickable added below via Button overlay
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(
                            onClick = viewModel::submit,
                            enabled = !formState.isSubmitting,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (formState.isSubmitting) {
                                CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text(
                                    if (formState.tab == AuthTab.LOGIN) stringResource(Res.string.auth_sign_in_button) else stringResource(Res.string.auth_create_account_button),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(LuxeSpacing.md))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(Modifier.weight(1f))
                        Text(
                            stringResource(Res.string.auth_or_continue_with),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(LuxeSpacing.md))

                    OutlinedButton(
                        onClick = viewModel::signInWithGoogle,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = LuxeShapes.full
                    ) {
                        Icon(painterResource(Res.drawable.ic_google), contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(LuxeSpacing.sm))
                        Text(stringResource(Res.string.auth_continue_google))
                    }

                    Spacer(Modifier.height(LuxeSpacing.sm))
                    Row {
                        Text(
                            if (formState.tab == AuthTab.LOGIN) stringResource(Res.string.auth_no_account_prompt) else stringResource(Res.string.auth_has_account_prompt),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            if (formState.tab == AuthTab.LOGIN) stringResource(Res.string.auth_tab_signup) else stringResource(Res.string.auth_tab_login),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                viewModel.onTabChanged(
                                    if (formState.tab == AuthTab.LOGIN)
                                        AuthTab.SIGN_UP
                                    else
                                        AuthTab.LOGIN
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    AuthStatusDialogHost(
        event = dialogEvent,
        onDismiss = { dialogEvent = null },
        onResendConfirmation = { email -> viewModel.resendConfirmation(email) },
        onSwitchToLogin = { viewModel.onTabChanged(AuthTab.LOGIN) }
    )
}

@Composable
private fun AuthTabToggle(current: AuthTab, onSelected: (AuthTab) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(LuxeShapes.full)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(4.dp)
    ) {
        listOf(AuthTab.LOGIN to stringResource(Res.string.auth_tab_login), AuthTab.SIGN_UP to stringResource(Res.string.auth_tab_signup)).forEach { (tab, label) ->
            val selected = current == tab
            Box(
                Modifier
                    .weight(1f)
                    .clip(LuxeShapes.full)
                    .background(
                        if (selected)
                            MaterialTheme.colorScheme.surfaceContainerLowest
                        else
                            Color.Transparent
                    )
                    .clickable { onSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: org.jetbrains.compose.resources.DrawableResource,
    isPassword: Boolean = false,
    isVisible: Boolean = false,
    onToggleVisibility: (() -> Unit)? = null
) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = {
                Icon(painterResource(leadingIcon), contentDescription = null, modifier = Modifier.size(20.dp))
            },
            trailingIcon = if (isPassword && onToggleVisibility != null) {
                {
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            painterResource(if (isVisible) Res.drawable.ic_eye_off else Res.drawable.ic_eye),
                            contentDescription = if (isVisible) stringResource(Res.string.auth_hide_password) else stringResource(Res.string.auth_show_password),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !isVisible) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true,
            shape = LuxeShapes.md,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Small helper so the tab labels and "Sign Up"/"Login" swap text are tappable
// without pulling in a full Button's padding/shape chrome.
