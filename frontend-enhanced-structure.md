// Frontend Enhanced Structure
//
// Berikut adalah struktur frontend yang ditingkatkan ke level enterprise untuk MyProject.nexa
//
// STRUKTUR YANG DITAMBAHKAN:
//
// 1. Core Structure
// src/
// └── core/
//     ├── api/
//     │   ├── axios/
//     │   │   ├── axios-instance.ts
//     │   │   ├── axios-interceptor.ts
//     │   │   └── axios-types.ts
//     │   ├── auth/
//     │   │   ├── auth-api.ts
//     │   │   └── auth-types.ts
//     │   ├── user/
//     │   │   ├── user-api.ts
//     │   │   └── user-types.ts
//     │   └── common/
//     │       ├── api-types.ts
//     │       └── response-wrapper.ts
//     ├── config/
//     │   ├── app-config.ts
//     │   ├── api-config.ts
//     │   └── env-config.ts
//     ├── hooks/
//     │   ├── useAuth.ts
//     │   ├── useUser.ts
//     │   ├── useApi.ts
//     │   └── ...
//     └── types/
//         ├── auth-types.ts
//         ├── user-types.ts
//         └── global-types.ts
//
// 2. Features Structure (sudah bagus, tinggal disempurnakan)
// src/features/
// └── auth/
//     ├── components/
//     │   ├── forms/
//     │   │   ├── LoginForm.tsx
//     │   │   └── RegisterForm.tsx
//     │   ├── layouts/
//     │   │   └── AuthLayout.tsx
//     │   └── ui/
//     │       ├── AuthCard.tsx
//     │       └── AuthFooter.tsx
//     ├── pages/
//     │   ├── LoginPage.tsx
//     │   ├── RegisterPage.tsx
//     │   └── ForgotPasswordPage.tsx
//     ├── services/
//     │   ├── AuthService.ts
//     │   └── useAuthService.ts
//     ├── hooks/
//     │   ├── useLogin.ts
//     │   └── useRegister.ts
//     ├── types/
//     │   └── auth-types.ts
//     └── constants/
//         └── auth-constants.ts
//
// 3. Shared Structure (sudah bagus, tinggal disempurnakan)
// src/shared/
// ├── components/
// │   ├── ui/
// │   │   ├── Button.tsx
// │   │   ├── Input.tsx
// │   │   ├── Card.tsx
// │   │   └── ...
// │   ├── layout/
// │   │   ├── Header.tsx
// │   │   ├── Sidebar.tsx
// │   │   └── Footer.tsx
// │   └── common/
// │       ├── ErrorBoundary.tsx
// │       ├── LoadingSpinner.tsx
// │       └── ...
// ├── hooks/
// ├── utils/
// │   ├── validators/
// │   ├── helpers/
// │   └── constants/
// └── types/
//     ├── api-types.ts
//     ├── ui-types.ts
//     └── common-types.ts
//
// PENINGKATAN YANG DIBUTUHKAN:
//
// 1. Gunakan TypeScript sepenuhnya
// 2. Terapkan React Query untuk server state management
// 3. Buat custom hooks untuk bisnis logic
// 4. Tambahkan Zustaand untuk global UI state (selain React Query)
// 5. Tambahkan skeleton loading & fallback UI
// 6. Implementasi proper error boundaries
// 7. Tambahkan API response wrapper
// 8. Tambahkan middleware untuk logging dan error handling