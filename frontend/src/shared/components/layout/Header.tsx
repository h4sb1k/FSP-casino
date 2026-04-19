import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@shared/hooks';
import { ROUTES } from '@shared/constants';
import { formatCurrency } from '@shared/utils/helpers';

export default function Header() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    // Логика logout будет реализована в auth service
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    navigate(ROUTES.LOGIN);
  };

  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-white border-b border-gray-200 shadow-sm">
      <div className="container-custom h-16 flex items-center justify-between">
        {/* Logo */}
        <Link to={ROUTES.DASHBOARD} className="flex items-center space-x-2">
          <div className="w-10 h-10 bg-gradient-to-br from-primary-500 to-primary-700 rounded-lg flex items-center justify-center">
            <span className="text-white font-bold text-xl">S</span>
          </div>
          <div>
            <h1 className="text-lg font-bold text-gray-900 leading-tight">Stoloto VIP</h1>
            <p className="text-xs text-gray-500 -mt-1">Premium Rooms</p>
          </div>
        </Link>

        {/* User info & actions */}
        <div className="flex items-center space-x-4">
          {/* Balance display */}
          {user && (
            <div className="hidden sm:flex items-center space-x-4">
              <div className="text-right">
                <p className="text-xs text-gray-500">Баланс</p>
                <p className="text-sm font-semibold text-gray-900">
                  {formatCurrency(user.balance)}
                </p>
              </div>
              {user.bonusBalance > 0 && (
                <div className="text-right">
                  <p className="text-xs text-gray-500">Бонусы</p>
                  <p className="text-sm font-semibold text-primary-600">
                    {formatCurrency(user.bonusBalance)}
                  </p>
                </div>
              )}
            </div>
          )}

          {/* User menu */}
          {user ? (
            <div className="flex items-center space-x-3">
              <Link
                to={ROUTES.PROFILE}
                className="flex items-center space-x-2 px-3 py-2 rounded-lg hover:bg-gray-100 transition-colors"
              >
                {user.avatarUrl ? (
                  <img
                    src={user.avatarUrl}
                    alt={user.username}
                    className="w-8 h-8 rounded-full object-cover"
                  />
                ) : (
                  <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center">
                    <span className="text-primary-600 font-medium text-sm">
                      {user.username[0].toUpperCase()}
                    </span>
                  </div>
                )}
                <span className="hidden md:block text-sm font-medium text-gray-700">
                  {user.username}
                </span>
              </Link>

              <button
                onClick={handleLogout}
                className="px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors"
              >
                Выход
              </button>
            </div>
          ) : (
            <div className="flex items-center space-x-2">
              <Link
                to={ROUTES.LOGIN}
                className="px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors"
              >
                Вход
              </Link>
              <Link
                to={ROUTES.REGISTER}
                className="px-4 py-2 text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 rounded-lg transition-colors shadow-sm"
              >
                Регистрация
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
