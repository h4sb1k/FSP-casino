import React from 'react';
import { Outlet } from 'react-router-dom';

export default function AuthLayout() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 via-white to-accent-50 flex">
      {/* Left side - branding */}
      <div className="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-primary-600 to-primary-800 text-white p-12 flex-col justify-between">
        <div>
          <div className="flex items-center space-x-3 mb-8">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
              <span className="text-white font-bold text-2xl">S</span>
            </div>
            <div>
              <h1 className="text-2xl font-bold">Stoloto VIP</h1>
              <p className="text-primary-200 text-sm">Premium Rooms</p>
            </div>
          </div>

          <div className="mt-12">
            <h2 className="text-4xl font-bold mb-4">Добро пожаловать в мир премиум игр</h2>
            <p className="text-primary-100 text-lg leading-relaxed">
              Эксклюзивные игровые комнаты, прозрачная система выигрышей 
              и максимальный уровень безопасности для ваших ставок.
            </p>
          </div>
        </div>

        <div className="space-y-4">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center">
              <span>🔒</span>
            </div>
            <span>Безопасность транзакций</span>
          </div>
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center">
              <span>⚡</span>
            </div>
            <span>Мгновенные выплаты</span>
          </div>
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center">
              <span>🎯</span>
            </div>
            <span>Честная игра</span>
          </div>
        </div>
      </div>

      {/* Right side - auth forms */}
      <div className="flex-1 flex items-center justify-center p-8">
        <div className="w-full max-w-md">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
