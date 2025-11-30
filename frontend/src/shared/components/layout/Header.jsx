import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/features/auth/hooks/useAuth';
import ThemeToggle from './ThemeToggle';
import {
    LogOut,
    User,
    Settings,
    Bell,
    Menu,
    X
} from 'lucide-react';
import { useState } from 'react';

const Header = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [showUserMenu, setShowUserMenu] = useState(false);
    const [showMobileMenu, setShowMobileMenu] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <header className="sticky top-0 z-50 w-full border-b border-border glass-effect backdrop-blur-professional">
            <div className="container mx-auto px-4">
                <div className="flex h-16 items-center justify-between">
                    {/* Logo */}
                    <Link to="/dashboard" className="flex items-center gap-3 group">
                        <div className="w-10 h-10 rounded-full bg-gradient-to-br from-[hsl(var(--yin))] to-[hsl(var(--yang))] flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                            <div className="w-6 h-6 rounded-full bg-card" />
                        </div>
                        <span className="text-xl font-bold hidden sm:block">
                            <span className="text-gradient-yin-yang">Domain.Nexa</span>
                        </span>
                    </Link>

                    {/* Desktop Navigation */}
                    <nav className="hidden md:flex items-center gap-6">
                        <Link
                            to="/dashboard"
                            className="text-sm font-medium text-muted-foreground hover:text-foreground transition-colors"
                        >
                            Dashboard
                        </Link>
                        <Link
                            to="/users"
                            className="text-sm font-medium text-muted-foreground hover:text-foreground transition-colors"
                        >
                            Users
                        </Link>
                        <Link
                            to="/settings"
                            className="text-sm font-medium text-muted-foreground hover:text-foreground transition-colors"
                        >
                            Settings
                        </Link>
                    </nav>

                    {/* Right Section */}
                    <div className="flex items-center gap-4">
                        {/* Theme Toggle */}
                        <ThemeToggle />

                        {/* Notifications */}
                        <button className="relative p-2 rounded-lg hover:bg-secondary transition-colors">
                            <Bell className="w-5 h-5" />
                            <span className="absolute top-1 right-1 w-2 h-2 bg-destructive rounded-full" />
                        </button>

                        {/* User Menu */}
                        <div className="relative">
                            <button
                                onClick={() => setShowUserMenu(!showUserMenu)}
                                className="flex items-center gap-2 p-2 rounded-lg hover:bg-secondary transition-colors"
                            >
                                <div className="w-8 h-8 rounded-full bg-gradient-to-br from-[hsl(var(--yin))] to-[hsl(var(--yang))] flex items-center justify-center text-white text-sm font-semibold">
                                    {user?.username?.charAt(0).toUpperCase() || 'U'}
                                </div>
                                <span className="hidden md:block text-sm font-medium">
                                    {user?.username || 'User'}
                                </span>
                            </button>

                            {/* Dropdown Menu */}
                            {showUserMenu && (
                                <>
                                    <div
                                        className="fixed inset-0 z-40"
                                        onClick={() => setShowUserMenu(false)}
                                    />
                                    <div className="absolute right-0 mt-2 w-56 rounded-xl border border-border bg-card shadow-balanced z-50 animate-slide-in-right">
                                        <div className="p-4 border-b border-border">
                                            <p className="text-sm font-semibold">{user?.username || 'User'}</p>
                                            <p className="text-xs text-muted-foreground">{user?.email || 'user@example.com'}</p>
                                        </div>
                                        <div className="p-2">
                                            <Link
                                                to="/profile"
                                                className="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-secondary transition-colors"
                                                onClick={() => setShowUserMenu(false)}
                                            >
                                                <User className="w-4 h-4" />
                                                <span className="text-sm">Profile</span>
                                            </Link>
                                            <Link
                                                to="/settings"
                                                className="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-secondary transition-colors"
                                                onClick={() => setShowUserMenu(false)}
                                            >
                                                <Settings className="w-4 h-4" />
                                                <span className="text-sm">Settings</span>
                                            </Link>
                                            <button
                                                onClick={handleLogout}
                                                className="w-full flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-destructive/10 text-destructive transition-colors"
                                            >
                                                <LogOut className="w-4 h-4" />
                                                <span className="text-sm">Logout</span>
                                            </button>
                                        </div>
                                    </div>
                                </>
                            )}
                        </div>

                        {/* Mobile Menu Button */}
                        <button
                            onClick={() => setShowMobileMenu(!showMobileMenu)}
                            className="md:hidden p-2 rounded-lg hover:bg-secondary transition-colors"
                        >
                            {showMobileMenu ? (
                                <X className="w-5 h-5" />
                            ) : (
                                <Menu className="w-5 h-5" />
                            )}
                        </button>
                    </div>
                </div>

                {/* Mobile Navigation */}
                {showMobileMenu && (
                    <div className="md:hidden py-4 border-t border-border animate-slide-in-right">
                        <nav className="flex flex-col gap-2">
                            <Link
                                to="/dashboard"
                                className="px-4 py-2 rounded-lg hover:bg-secondary transition-colors"
                                onClick={() => setShowMobileMenu(false)}
                            >
                                Dashboard
                            </Link>
                            <Link
                                to="/users"
                                className="px-4 py-2 rounded-lg hover:bg-secondary transition-colors"
                                onClick={() => setShowMobileMenu(false)}
                            >
                                Users
                            </Link>
                            <Link
                                to="/settings"
                                className="px-4 py-2 rounded-lg hover:bg-secondary transition-colors"
                                onClick={() => setShowMobileMenu(false)}
                            >
                                Settings
                            </Link>
                        </nav>
                    </div>
                )}
            </div>
        </header>
    );
};

export default Header;
