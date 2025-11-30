import React from 'react';
import { Button } from '../ui/button';
import { useAuth } from '../../../features/auth/hooks/useAuth';
import { Link } from 'react-router-dom';
import { useTheme } from '../../../app/providers/ThemeProvider';
import { Moon, Sun, Palette } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from '../ui/dropdown-menu';

const Navbar = () => {
  const { user, logout } = useAuth();
  const { theme, toggleTheme, setTheme } = useTheme();

  const themes = [
    { id: 'yin-yang', name: 'Yin Yang', icon: Palette },
    { id: 'light', name: 'Light', icon: Sun },
    { id: 'dark', name: 'Dark', icon: Moon },
    { id: 'ocean', name: 'Ocean', icon: Palette },
    { id: 'forest', name: 'Forest', icon: Palette },
    { id: 'sunset', name: 'Sunset', icon: Palette },
    { id: 'midnight', name: 'Midnight', icon: Moon },
  ];

  const currentTheme = themes.find(t => t.id === theme) || themes[0];
  const CurrentThemeIcon = currentTheme.icon;

  return (
    <nav className="border-b">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <Link to="/" className="flex-shrink-0 flex items-center">
              <span className="text-xl font-bold text-indigo-600">MyProject.nexa</span>
            </Link>
          </div>

          <div className="flex items-center space-x-4">
            {/* Theme Selector */}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon" className="rounded-full">
                  <CurrentThemeIcon className="h-5 w-5" />
                  <span className="sr-only">Toggle theme</span>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                {themes.map((themeOption) => {
                  const ThemeIcon = themeOption.icon;
                  return (
                    <DropdownMenuItem
                      key={themeOption.id}
                      onClick={() => setTheme(themeOption.id)}
                      className={theme === themeOption.id ? 'bg-accent' : ''}
                    >
                      <ThemeIcon className="mr-2 h-4 w-4" />
                      <span>{themeOption.name}</span>
                      {theme === themeOption.id && (
                        <span className="ml-auto text-xs text-green-500">✓ Active</span>
                      )}
                    </DropdownMenuItem>
                  );
                })}
              </DropdownMenuContent>
            </DropdownMenu>

            {user ? (
              <div className="flex items-center space-x-4">
                <span className="text-sm">Welcome, {user.username}</span>
                <Button onClick={logout} variant="outline">Logout</Button>
              </div>
            ) : (
              <div className="flex items-center space-x-4">
                <Link to="/login">
                  <Button variant="outline">Login</Button>
                </Link>
                <Link to="/register">
                  <Button>Register</Button>
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;