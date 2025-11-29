import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Button } from '../ui/button';
import { 
  LayoutDashboard, 
  Users, 
  Settings,
  LogOut 
} from 'lucide-react';
import { useAuth } from '../../../features/auth/hooks/useAuth';

const Sidebar = () => {
  const { logout } = useAuth();
  const location = useLocation();

  const navItems = [
    { 
      name: 'Dashboard', 
      path: '/dashboard', 
      icon: LayoutDashboard 
    },
    { 
      name: 'Users', 
      path: '/users', 
      icon: Users 
    },
    { 
      name: 'Settings', 
      path: '/settings', 
      icon: Settings 
    },
  ];

  return (
    <div className="w-64 border-r h-full flex flex-col">
      <div className="p-4 border-b">
        <h2 className="text-xl font-bold">Navigation</h2>
      </div>
      
      <nav className="flex-1 p-4">
        <ul className="space-y-2">
          {navItems.map((item) => (
            <li key={item.path}>
              <Link to={item.path}>
                <Button
                  variant={location.pathname === item.path ? 'secondary' : 'ghost'}
                  className={`w-full justify-start ${location.pathname === item.path ? 'bg-indigo-100' : ''}`}
                >
                  <item.icon className="mr-2 h-4 w-4" />
                  {item.name}
                </Button>
              </Link>
            </li>
          ))}
        </ul>
      </nav>
      
      <div className="p-4 border-t">
        <Button 
          variant="outline" 
          className="w-full" 
          onClick={logout}
        >
          <LogOut className="mr-2 h-4 w-4" />
          Logout
        </Button>
      </div>
    </div>
  );
};

export default Sidebar;