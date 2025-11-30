import React from 'react';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/shared/components/ui/card';
import { Table, TableHeader, TableBody, TableRow, TableCell, TableHead } from '@/shared/components/ui/table';
import useApi from '@/shared/hooks/useApi';
import {
  Users,
  Activity,
  DollarSign,
  TrendingUp,
  Clock,
  ArrowUpRight,
  ArrowDownRight,
  BarChart3,
  PieChart,
  Calendar
} from 'lucide-react';
import { DomainAdvantages } from '@/shared/components/ui/DomainAdvantages';

const DashboardPage = () => {
  // Example usage of useApi hook - in real app, you'd fetch actual dashboard data
  // For this example, we'll use mock data and avoid calling any APIs that might cause errors
  // const { get } = useApi();

  // Example: getting dashboard stats
  // const { data: stats, isLoading: statsLoading, error: statsError } = get('/api/v1/dashboard/stats');

  // For this example, we'll use mock data
  const stats = [
    {
      title: 'Total Users',
      value: '1,234',
      change: '+12.5%',
      trend: 'up',
      icon: Users,
      color: 'from-blue-500 to-cyan-500'
    },
    {
      title: 'Active Sessions',
      value: '456',
      change: '+8.2%',
      trend: 'up',
      icon: Activity,
      color: 'from-emerald-500 to-teal-500'
    },
    {
      title: 'Revenue',
      value: '$12,345',
      change: '+23.1%',
      trend: 'up',
      icon: DollarSign,
      color: 'from-violet-500 to-purple-500'
    },
    {
      title: 'Growth Rate',
      value: '+12.5%',
      change: '-2.4%',
      trend: 'down',
      icon: TrendingUp,
      color: 'from-orange-500 to-red-500'
    },
  ];

  const recentActivity = [
    {
      id: 1,
      user: 'John Doe',
      action: 'Created new project',
      time: '2 min ago',
      avatar: 'JD',
      type: 'create'
    },
    {
      id: 2,
      user: 'Jane Smith',
      action: 'Updated settings',
      time: '15 min ago',
      avatar: 'JS',
      type: 'update'
    },
    {
      id: 3,
      user: 'Bob Johnson',
      action: 'Uploaded document',
      time: '1 hour ago',
      avatar: 'BJ',
      type: 'upload'
    },
    {
      id: 4,
      user: 'Alice Brown',
      action: 'Commented on task',
      time: '3 hours ago',
      avatar: 'AB',
      type: 'comment'
    },
  ];

  const quickActions = [
    { icon: Users, label: 'Manage Users', color: 'bg-blue-500' },
    { icon: BarChart3, label: 'View Analytics', color: 'bg-emerald-500' },
    { icon: PieChart, label: 'Reports', color: 'bg-violet-500' },
    { icon: Calendar, label: 'Schedule', color: 'bg-orange-500' },
  ];

  return (
    <div className="space-y-8 p-6 animate-fade-in">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-bold mb-2">
            <span className="text-gradient-yin-yang">Dashboard</span>
          </h1>
          <p className="text-muted-foreground text-lg">
            Welcome back! Here's what's happening today.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Clock className="w-5 h-5 text-muted-foreground" />
          <span className="text-sm text-muted-foreground">
            {new Date().toLocaleDateString('en-US', {
              weekday: 'long',
              year: 'numeric',
              month: 'long',
              day: 'numeric'
            })}
          </span>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <Card
              key={index}
              className="card-professional hover-lift overflow-hidden relative group"
              style={{ animationDelay: `${index * 100}ms` }}
            >
              {/* Gradient Background */}
              <div className={`absolute inset-0 bg-gradient-to-br ${stat.color} opacity-0 group-hover:opacity-10 transition-opacity duration-300`} />

              <CardHeader className="flex flex-row items-center justify-between pb-2 relative z-10">
                <CardDescription className="text-sm font-medium">
                  {stat.title}
                </CardDescription>
                <div className={`p-2 rounded-lg bg-gradient-to-br ${stat.color}`}>
                  <Icon className="w-5 h-5 text-white" />
                </div>
              </CardHeader>

              <CardContent className="relative z-10">
                <div className="flex items-baseline justify-between">
                  <div className="text-3xl font-bold">{stat.value}</div>
                  <div className={`flex items-center gap-1 text-sm font-semibold ${stat.trend === 'up' ? 'text-green-500' : 'text-red-500'
                    }`}>
                    {stat.trend === 'up' ? (
                      <ArrowUpRight className="w-4 h-4" />
                    ) : (
                      <ArrowDownRight className="w-4 h-4" />
                    )}
                    {stat.change}
                  </div>
                </div>

                {/* Progress Bar */}
                <div className="mt-4 h-2 bg-secondary rounded-full overflow-hidden">
                  <div
                    className={`h-full bg-gradient-to-r ${stat.color} rounded-full transition-all duration-1000 ease-out`}
                    style={{
                      width: `${Math.min(100, Math.floor(Math.random() * 100) + 1)}%`,
                      animationDelay: `${index * 100}ms`
                    }}
                  />
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      {/* Quick Actions */}
      <Card className="card-professional">
        <CardHeader>
          <CardTitle className="text-2xl">Quick Actions</CardTitle>
          <CardDescription>Frequently used features</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {quickActions.map((action, index) => {
              const Icon = action.icon;
              return (
                <button
                  key={index}
                  className="flex flex-col items-center gap-3 p-6 rounded-xl border border-border hover:border-primary transition-all duration-300 hover-lift group"
                >
                  <div className={`${action.color} p-4 rounded-xl group-hover:scale-110 transition-transform duration-300`}>
                    <Icon className="w-6 h-6 text-white" />
                  </div>
                  <span className="text-sm font-medium text-center">{action.label}</span>
                </button>
              );
            })}
          </div>
        </CardContent>
      </Card>

      {/* Recent Activity */}
      <Card className="card-professional">
        <CardHeader>
          <div className="flex items-center justify-between">
            <div>
              <CardTitle className="text-2xl">Recent Activity</CardTitle>
              <CardDescription>Your team's latest actions</CardDescription>
            </div>
            <button className="text-sm text-primary hover:underline font-medium">
              View All
            </button>
          </div>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {recentActivity.map((activity, index) => (
              <div
                key={activity.id}
                className="flex items-center gap-4 p-4 rounded-lg hover:bg-secondary/50 transition-colors duration-200 group"
                style={{ animationDelay: `${index * 50}ms` }}
              >
                {/* Avatar */}
                <div className="w-12 h-12 rounded-full bg-gradient-to-br from-[hsl(var(--yin))] to-[hsl(var(--yang))] flex items-center justify-center text-white font-semibold group-hover:scale-110 transition-transform duration-300">
                  {activity.avatar}
                </div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <p className="font-semibold text-foreground truncate">
                    {activity.user}
                  </p>
                  <p className="text-sm text-muted-foreground truncate">
                    {activity.action}
                  </p>
                </div>

                {/* Time */}
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <Clock className="w-4 h-4" />
                  {activity.time}
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Performance Chart */}
        <Card className="card-professional">
          <CardHeader>
            <CardTitle className="text-xl">Performance Overview</CardTitle>
            <CardDescription>Last 30 days</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-64 flex items-center justify-center bg-secondary/30 rounded-lg">
              <div className="text-center">
                <BarChart3 className="w-16 h-16 mx-auto text-muted-foreground mb-2" />
                <p className="text-sm text-muted-foreground">Chart visualization</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Distribution Chart */}
        <Card className="card-professional">
          <CardHeader>
            <CardTitle className="text-xl">User Distribution</CardTitle>
            <CardDescription>By category</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-64 flex items-center justify-center bg-secondary/30 rounded-lg">
              <div className="text-center">
                <PieChart className="w-16 h-16 mx-auto text-muted-foreground mb-2" />
                <p className="text-sm text-muted-foreground">Chart visualization</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Domain Strategic Advantages */}
      <div className="mt-8">
        <DomainAdvantages />
      </div>
    </div>
  );
};

export default DashboardPage;