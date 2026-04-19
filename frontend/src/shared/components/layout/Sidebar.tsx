import React from 'react';
import { NavLink } from 'react-router-dom';
import { ROUTES } from '@shared/constants';
import { useRole } from '@shared/hooks';

const navigation = [
  { name: 'Дашборд', href: ROUTES.DASHBOARD, icon: '📊' },
  { name: 'Лобби', href: ROUTES.LOBBY, icon: '🎮' },
  { name: 'Профиль', href: ROUTES.PROFILE, icon: '👤' },
  { name: 'Транзакции', href: ROUTES.TRANSACTIONS, icon: '💳' },
  { name: 'История', href: ROUTES.HISTORY, icon: '📜' },
];

const adminNavigation = [
  { name: 'Дашборд', href: ROUTES.ADMIN_DASHBOARD, icon: '📈' },
  { name: 'Комнаты', href: ROUTES.ADMIN_ROOMS, icon: '🏠' },
  { name: 'Боты', href: ROUTES.ADMIN_BOTS, icon: '🤖' },
  { name: 'Бусты', href: ROUTES.ADMIN_BOOSTS, icon: '⚡' },
  { name: 'Экономика', href: ROUTES.ADMIN_ECONOMY, icon: '💰' },
  { name: 'Аудит', href: ROUTES.ADMIN_AUDIT, icon: '📋' },
  { name: 'Аналитика', href: ROUTES.ADMIN_ANALYTICS, icon: '📉' },
];

export default function Sidebar() {
  const { hasMinRole } = useRole();
  const isAdmin = hasMinRole('ADMIN');

  return (
    <nav className="h-full overflow-y-auto py-4">
      {/* Main navigation */}
      <div className="px-3">
        <ul className="space-y-1">
          {navigation.map((item) => (
            <li key={item.name}>
              <NavLink
                to={item.href}
                className={({ isActive }) =>
                  `flex items-center space-x-3 px-3 py-2.5 rounded-lg transition-all duration-200 ${
                    isActive
                      ? 'bg-primary-50 text-primary-700 font-medium'
                      : 'text-gray-700 hover:bg-gray-100'
                  }`
                }
              >
                <span className="text-lg">{item.icon}</span>
                <span>{item.name}</span>
              </NavLink>
            </li>
          ))}
        </ul>
      </div>

      {/* Admin navigation */}
      {isAdmin && (
        <div className="mt-8 px-3">
          <div className="mb-2 px-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">
            Администрирование
          </div>
          <ul className="space-y-1">
            {adminNavigation.map((item) => (
              <li key={item.name}>
                <NavLink
                  to={item.href}
                  className={({ isActive }) =>
                    `flex items-center space-x-3 px-3 py-2.5 rounded-lg transition-all duration-200 ${
                      isActive
                        ? 'bg-accent-50 text-accent-700 font-medium'
                        : 'text-gray-700 hover:bg-gray-100'
                    }`
                  }
                >
                  <span className="text-lg">{item.icon}</span>
                  <span>{item.name}</span>
                </NavLink>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Footer info */}
      <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-200 bg-white">
        <p className="text-xs text-gray-500 text-center">
          Stoloto VIP Rooms © {new Date().getFullYear()}
        </p>
      </div>
    </nav>
  );
}
