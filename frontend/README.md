# MyProject.nexa Frontend

This is the frontend application for the MyProject.nexa enterprise application, built with React, Vite, and TailwindCSS.

## Tech Stack

- **React 18**: JavaScript library for building user interfaces
- **Vite**: Next-generation frontend build tool
- **TailwindCSS**: Utility-first CSS framework
- **React Router v6**: Declarative routing for React
- **Axios**: Promise-based HTTP client
- **TanStack Query (React Query)**: Server state management
- **shadcn/ui**: Re-usable components built using Radix UI and Tailwind CSS

## Architecture

The application follows a feature-based architecture with the following structure:

```
src/
├── app/                    # Main application configuration
│   ├── App.jsx            # Main application component
│   ├── AppRoutes.jsx      # Application routing
│   └── providers/         # React Context providers
├── features/              # Feature modules
│   ├── auth/              # Authentication feature
│   ├── dashboard/         # Dashboard feature
│   └── users/             # User management feature
├── shared/                # Shared components, hooks, and utilities
│   ├── components/        # Reusable UI components
│   ├── hooks/             # Shared React hooks
│   └── utils/             # Utility functions
├── core/                  # Core application logic
│   ├── api/               # API client and endpoints
│   ├── auth/              # Authentication services
│   └── config/            # Application configuration
├── assets/                # Static assets
└── styles/                # Global styles
```

## Environment Variables

Create a `.env` file in the root of the frontend directory:

```env
VITE_API_BASE_URL=https://api.myproject.nexa
VITE_APP_NAME=MyProject.nexa
VITE_ENVIRONMENT=development
```

## Getting Started

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

3. Open [http://localhost:3000](http://localhost:3000) in your browser

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## Key Features

- **Authentication**: Complete auth flow with login, registration, and token management
- **Protected Routes**: Role-based access control
- **API Integration**: Centralized API client with interceptors
- **State Management**: React Query for server state and custom hooks for local state
- **Responsive Design**: Mobile-first responsive UI
- **Component Library**: Reusable UI components following design system
- **Form Validation**: Client-side validation with user feedback

## API Integration

The application uses a centralized API client located at `src/core/api/apiClient.js` that:
- Handles authentication tokens
- Intercepts requests to add auth headers
- Automatically refreshes expired tokens
- Redirects to login on authentication failure