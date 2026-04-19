import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '@shared/hooks';
import { ROUTES } from '@shared/constants';

// Layouts
import MainLayout from './shared/components/layout/MainLayout';
import AuthLayout from './auth/layouts/AuthLayout';

// Auth pages
import LoginPage from './auth/pages/LoginPage';
import RegisterPage from './auth/pages/RegisterPage';

// User pages
import DashboardPage from './user/pages/DashboardPage';
import LobbyPage from './lobby/pages/LobbyPage';
import RoomPage from './game/pages/RoomPage';
import ProfilePage from './user/pages/ProfilePage';
import TransactionsPage from './user/pages/TransactionsPage';
import HistoryPage from './user/pages/HistoryPage';

// Admin pages
import AdminDashboardPage from './admin/pages/AdminDashboardPage';
import AdminRoomsPage from './admin/pages/AdminRoomsPage';
import AdminBotsPage from './admin/pages/AdminBotsPage';
import AdminBoostsPage from './admin/pages/AdminBoostsPage';
import AdminEconomyPage from './admin/pages/AdminEconomyPage';
import AdminAuditPage from './admin/pages/AdminAuditPage';
import AdminAnalyticsPage from './admin/pages/AdminAnalyticsPage';

// Protected route wrapper
interface ProtectedRouteProps {
  children: React.ReactNode;
  minRole?: string;
}

function ProtectedRoute({ children, minRole }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading, user } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to={ROUTES.LOGIN} replace />;
  }

  if (minRole && user) {
    const rolePriority: Record<string, number> = {
      GUEST: 0,
      USER: 1,
      MODERATOR: 2,
      ADMIN: 3,
      SUPER_ADMIN: 4,
    };

    const userPriority = rolePriority[user.role] ?? 0;
    const requiredPriority = rolePriority[minRole] ?? 0;

    if (userPriority < requiredPriority) {
      return <Navigate to={ROUTES.DASHBOARD} replace />;
    }
  }

  return <>{children}</>;
}

function App() {
  return (
    <Routes>
      {/* Public routes */}
      <Route element={<AuthLayout />}>
        <Route path={ROUTES.LOGIN} element={<LoginPage />} />
        <Route path={ROUTES.REGISTER} element={<RegisterPage />} />
      </Route>

      {/* Protected user routes */}
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to={ROUTES.DASHBOARD} replace />} />
        <Route path={ROUTES.DASHBOARD} element={<DashboardPage />} />
        <Route path={ROUTES.LOBBY} element={<LobbyPage />} />
        <Route path={ROUTES.ROOM} element={<RoomPage />} />
        <Route path={ROUTES.PROFILE} element={<ProfilePage />} />
        <Route path={ROUTES.TRANSACTIONS} element={<TransactionsPage />} />
        <Route path={ROUTES.HISTORY} element={<HistoryPage />} />

        {/* Admin routes */}
        <Route
          path={ROUTES.ADMIN}
          element={
            <ProtectedRoute minRole="ADMIN">
              <Navigate to={ROUTES.ADMIN_DASHBOARD} replace />
            </ProtectedRoute>
          }
        />
        <Route
          path={ROUTES.ADMIN_DASHBOARD}
          element={
            <ProtectedRoute minRole="ADMIN">
              <AdminDashboardPage />
            </ProtectedRoute>
          }
        />
        <Route
          path={ROUTES.ADMIN_ROOMS}
          element={
            <ProtectedRoute minRole="ADMIN">
              <AdminRoomsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path={ROUTES.ADMIN_BOTS}
          element={
            <ProtectedRoute minRole="ADMIN">
              <AdminBotsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path={ROUTES.ADMIN_BOOSTS}
          element={
            <ProtectedRoute minRole="ADMIN">
              <AdminBoostsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path={ROUTES.ADMIN_ECONOMY}
          element={
            <ProtectedRoute minRole="ADMIN">
              <AdminEconomyPage />
            </ProtectedRoute>
          }
        />
        <Route
          path={ROUTES.ADMIN_AUDIT}
          element={
            <ProtectedRoute minRole="ADMIN">
              <AdminAuditPage />
            </ProtectedRoute>
          }
        />
        <Route
          path={ROUTES.ADMIN_ANALYTICS}
          element={
            <ProtectedRoute minRole="ADMIN">
              <AdminAnalyticsPage />
            </ProtectedRoute>
          }
        />
      </Route>

      {/* 404 */}
      <Route path="*" element={<Navigate to={ROUTES.DASHBOARD} replace />} />
    </Routes>
  );
}

export default App;
