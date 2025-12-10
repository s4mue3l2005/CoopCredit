# 🏦 CoopCredit Frontend

Frontend web application for the CoopCredit credit management system.

## 🚀 Prerequisites

- **Node.js** 18+ 
- **npm** or **yarn**
- **Backend API** running at `http://localhost:8080`

## 📦 Installation

```bash
# Install dependencies
npm install
```

## 🏃 Run Locally

```bash
# Start development server
npm run dev
```

The app will be available at **http://localhost:5173**

## 🔑 First Steps

1. **Start the backend** first (make sure it's running on port 8080)
2. **Open** http://localhost:5173
3. **Register** a new user
4. **Login** with your credentials
5. **Create** an affiliate
6. **Request** a credit

## 🎯 Main Features

- ✅ User authentication (JWT)
- ✅ Affiliate management
- ✅ Credit requests
- ✅ Real-time risk evaluation
- ✅ Credit status tracking
- ✅ Responsive design

## 📱 Pages

- `/login` - User login
- `/register` - User registration
- `/` - Dashboard with statistics
- `/affiliates` - Affiliate management
- `/credits` - Credit requests

## 🛠️ Tech Stack

- **React 19** with TypeScript
- **Vite** - Build tool
- **Axios** - HTTP client
- **React Router** - Routing
- **Tailwind CSS** - Styling (inline)

## 🔗 API Connection

The frontend connects to: `http://localhost:8080/api`

Endpoints:
- `POST /auth/register` - Register user
- `POST /auth/login` - Login
- `GET /affiliates` - List affiliates
- `POST /affiliates` - Create affiliate
- `GET /credits` - List credits
- `POST /credits` - Request credit

## 📝 Build for Production

```bash
npm run build
```

Output will be in `dist/` folder.
