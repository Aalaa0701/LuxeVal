//package com.example.luxeval.presentation.auth
//
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import com.example.luxeval.presentation.theme.LuxeSpacing
//
//@Composable
//fun SignupScreen(viewModel: AuthViewModel, onNavigateToLogin: () -> Unit) {
//    val formState by viewModel.formState.collectAsState()
//
//    Scaffold { padding ->
//        Column(
//            Modifier.fillMaxSize().padding(padding).padding(LuxeSpacing.lg),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text("Welcome", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
//            Spacer(Modifier.height(LuxeSpacing.xs))
//            Text(
//                "Sign Up to Start your appraisals.",
//                style = MaterialTheme.typography.bodyMedium
//            )
//            Spacer(Modifier.height(LuxeSpacing.lg))
//
//            OutlinedTextField(
//                value = formState.email,
//                onValueChange = viewModel::onEmailChanged,
//                label = { Text("Email") },
//                singleLine = true,
//                modifier = Modifier.fillMaxWidth()
//            )
//            Spacer(Modifier.height(LuxeSpacing.sm))
//            OutlinedTextField(
//                value = formState.password,
//                onValueChange = viewModel::onPasswordChanged,
//                label = { Text("Password") },
//                singleLine = true,
//                visualTransformation = PasswordVisualTransformation(),
//                modifier = Modifier.fillMaxWidth()
//            )
//            OutlinedTextField(
//                value = formState.confirmPassword,
//                onValueChange = viewModel::onConfirmPasswordChanged,
//                label = { Text("Confirm Password") },
//                singleLine = true,
//                visualTransformation = PasswordVisualTransformation(),
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            formState.error?.let {
//                Spacer(Modifier.height(LuxeSpacing.xs))
//                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
//            }
//
//            Spacer(Modifier.height(LuxeSpacing.md))
//            Button(
//                onClick = { viewModel.signUp(onSuccess = { /* no-op: RootNavHost reacts to authState */ }) },
//                enabled = !formState.isSubmitting,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                if (formState.isSubmitting) {
//                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
//                } else {
//                    Text("Sign Up")
//                }
//            }
//
//            Spacer(Modifier.height(LuxeSpacing.md))
//            Row(
//                Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("have an account? ")
//                TextButton(onClick = onNavigateToLogin) { Text("Sign in") }
//            }
//        }
//    }
//}