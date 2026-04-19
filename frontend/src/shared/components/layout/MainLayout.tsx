import React from 'react';
import { Outlet } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';

interface MainLayoutProps {
  children?: React.ReactNode;
}

export default function MainLayout({ children }: MainLayoutProps) {
  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <Header />

      <div className="flex">
        {/* Sidebar - скрывается на мобильных */}
        <aside className="hidden lg:block w-64 fixed inset-y-0 left-0 pt-16 bg-white border-r border-gray-200">
          <Sidebar />
        </aside>

        {/* Main content */}
        <main className="flex-1 lg:ml-64 pt-16">
          <div className="container-custom py-6">
            {children || <Outlet />}
          </div>
        </main>
      </div>
    </div>
  );
}
