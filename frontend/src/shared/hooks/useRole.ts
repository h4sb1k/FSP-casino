import { useState, useEffect, useCallback } from 'react';
import type { User } from '@shared/types';
import { ROLE_PRIORITY } from '@shared/constants';

interface UseRoleOptions {
  requiredRole?: string;
  minRole?: string;
}

interface UseRoleResult {
  hasAccess: boolean;
  hasRole: (role: string) => boolean;
  hasMinRole: (minRole: string) => boolean;
  userRole: string | null;
}

export function useRole(options: UseRoleOptions = {}): UseRoleResult {
  const { requiredRole, minRole } = options;
  const [userRole, setUserRole] = useState<string | null>(null);

  useEffect(() => {
    // Получаем роль пользователя из localStorage или контекста
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      try {
        const user: User = JSON.parse(storedUser);
        setUserRole(user.role);
      } catch {
        setUserRole(null);
      }
    }
  }, []);

  const hasRole = useCallback(
    (role: string): boolean => {
      if (!userRole) return false;
      return userRole === role;
    },
    [userRole]
  );

  const hasMinRole = useCallback(
    (minimumRole: string): boolean => {
      if (!userRole || !minimumRole) return false;
      const userPriority = ROLE_PRIORITY[userRole as keyof typeof ROLE_PRIORITY] ?? 0;
      const requiredPriority = ROLE_PRIORITY[minimumRole as keyof typeof ROLE_PRIORITY] ?? 0;
      return userPriority >= requiredPriority;
    },
    [userRole]
  );

  const hasAccess = useCallback((): boolean => {
    if (requiredRole) {
      return hasRole(requiredRole);
    }
    if (minRole) {
      return hasMinRole(minRole);
    }
    return !!userRole;
  }, [requiredRole, minRole, hasRole, hasMinRole, userRole]);

  return {
    hasAccess: hasAccess(),
    hasRole,
    hasMinRole,
    userRole,
  };
}

/**
 * HOC для защиты компонентов по роли
 */
export function withRole<P extends object>(
  WrappedComponent: React.ComponentType<P>,
  options: UseRoleOptions
) {
  return function WithRole(props: P) {
    const { hasAccess } = useRole(options);

    if (!hasAccess) {
      return null; // Или можно показать компонент "Нет доступа"
    }

    return <WrappedComponent {...props} />;
  };
}

/**
 * Hook для проверки конкретной роли администратора
 */
export function useAdminRole() {
  return useRole({ minRole: 'ADMIN' });
}

/**
 * Hook для проверки роли модератора
 */
export function useModeratorRole() {
  return useRole({ minRole: 'MODERATOR' });
}
