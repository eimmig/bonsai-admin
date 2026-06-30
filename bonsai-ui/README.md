# Bonsai UI

React + TypeScript admin panel for the Bonsai project, consuming the `bonsai-admin` Spring Boot API.

## Stack

| | |
|---|---|
| **React** | 19 |
| **TypeScript** | 5.9 |
| **Vite** | rolldown-vite 7 |
| **Tailwind CSS** | v4 (via `@tailwindcss/vite`) |
| **HTTP client** | Axios |
| **Icons** | Lucide React |

## Features

- **Authentication** — JWT login/logout with token persisted in `localStorage`; session-expired 401 auto-redirects to login without disrupting the login-page 401 error flow
- **User registration** — self-service signup with admin-activation gate
- **User management** — list, search, activate inactive accounts, assign roles (ADMIN / OPERATOR)
- **Dashboard overview** — counters for total users and orders, order counts by status, last-refresh timestamp
- **Orders** — paginated list with filters by status, customer (searchable dropdown), and date range; inline status update with confirmation dialog; full status-change history
- **Attachments** — upload (Invoice/Receipt/Other) and download attachments per order; searchable order picker that shows customer name, status and creation date
- **Standardized error handling** — all API errors surface the backend's `ProblemDetail` (`detail` field and field-level `errors` map) in toast notifications; blob-typed download errors are read asynchronously before display
- **Toast notifications** — success toasts auto-dismiss after 4 s; error toasts persist until dismissed
- **Confirmation dialogs** — custom modal (not `window.confirm`) matching the app theme, used for activate-user, assign-roles and update-status actions
- **Pagination** — backend-driven page controls on all list views

## Project structure

```
src/
├── api/             # Axios call wrappers per resource (auth, users, orders, attachments)
├── components/
│   ├── ConfirmDialog.tsx   # Imperative Promise-based confirm modal
│   ├── CustomSelect.tsx    # Styled searchable dropdown (replaces <select>)
│   ├── EmptyState.tsx      # Empty list placeholder
│   ├── Pagination.tsx      # Page controls
│   └── Toast.tsx           # Toast notification system
├── hooks/
│   ├── useAuth.ts          # Login, register, logout state
│   ├── useAttachments.ts   # Upload / download logic
│   ├── useOrders.ts        # Order list, filters, status update, history
│   └── useUsers.ts         # User list, activate, assign roles
├── lib/
│   ├── api-client.ts       # Axios instance with JWT interceptor
│   ├── error.ts            # ProblemDetail extraction helpers
│   ├── format.ts           # Status/role/type labels, currency (USD), date (en-US)
│   └── storage.ts          # localStorage token/user helpers
├── types.ts                # Shared TypeScript interfaces
├── views/                  # One component per section (Login, Register, Overview, Users, Orders, Files)
├── App.tsx                 # Root: layout, navigation, dashboard refresh
└── index.css               # Tailwind v4 + theme tokens + component layer
```

## Backend API endpoints consumed

| Method | Path | Used for |
|---|---|---|
| `POST` | `/auth/login` | Login |
| `POST` | `/users/register` | Register |
| `GET` | `/users` | User list (paginated, searchable) |
| `PUT` | `/users/{id}/activate` | Activate user |
| `PUT` | `/users/{id}/roles` | Assign roles |
| `GET` | `/orders` | Order list (paginated, filterable) |
| `GET` | `/orders/summary` | Order counts by status |
| `PUT` | `/orders/{id}/status` | Update order status |
| `GET` | `/orders/{id}/history` | Status change history |
| `GET` | `/orders/{orderId}/attachments` | List attachments |
| `POST` | `/orders/{orderId}/attachments` | Upload attachment |
| `GET` | `/attachments/{id}/download` | Download attachment |

> The API has no `/api` prefix. The default `baseURL` is `http://localhost:8080`.

## Environment variables

Create a `.env` file at the project root:

```env
VITE_API_BASE_URL=http://localhost:8080
```

If omitted, `http://localhost:8080` is used as the default.

## Getting started

```bash
# install dependencies
npm install

# start dev server
npm run dev

# production build (also runs TypeScript check)
npm run build

# preview production build locally
npm run preview
```

1. Start the `bonsai-admin` backend.
2. Set `VITE_API_BASE_URL` if the backend runs on a different host/port.
3. Run `npm run dev` and open the URL shown by Vite.

## Seed data

The backend migration `V5__insert_seed_data.sql` includes:

- 2 pre-created users: `admin@utfpr.edu.br` (ADMIN) and `operador@utfpr.edu.br` (OPERATOR) — password `admin1` for both
- 7 customers and 14 orders covering all five order statuses, each with multiple bonsai-themed order items
