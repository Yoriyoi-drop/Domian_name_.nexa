import React from 'react';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '../../shared/components/ui/card';
import { Table, TableHeader, TableBody, TableRow, TableCell, TableHead } from '../../shared/components/ui/table';
import { useApi } from '../../shared/hooks/useApi';

const DashboardPage = () => {
  // Example usage of useApi hook - in real app, you'd fetch actual dashboard data
  const { get } = useApi();
  
  // Example: getting dashboard stats
  // const { data: stats, isLoading: statsLoading, error: statsError } = get('/api/v1/dashboard/stats');
  
  // For this example, we'll use mock data
  const stats = [
    { title: 'Total Users', value: '1,234' },
    { title: 'Active Sessions', value: '456' },
    { title: 'Revenue', value: '$12,345' },
    { title: 'Growth', value: '+12.5%' },
  ];
  
  const recentActivity = [
    { id: 1, user: 'John Doe', action: 'Created new project', time: '2 min ago' },
    { id: 2, user: 'Jane Smith', action: 'Updated settings', time: '15 min ago' },
    { id: 3, user: 'Bob Johnson', action: 'Uploaded document', time: '1 hour ago' },
    { id: 4, user: 'Alice Brown', action: 'Commented on task', time: '3 hours ago' },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Dashboard</h1>
        <p className="text-gray-600">Welcome back! Here's what's happening today.</p>
      </div>
      
      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => (
          <Card key={index}>
            <CardHeader>
              <CardTitle>{stat.value}</CardTitle>
              <CardDescription>{stat.title}</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-2 bg-gray-200 rounded-full">
                <div 
                  className="h-full bg-indigo-600 rounded-full" 
                  style={{ width: `${Math.min(100, Math.floor(Math.random() * 100) + 1)}%` }}
                ></div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
      
      {/* Recent Activity */}
      <Card>
        <CardHeader>
          <CardTitle>Recent Activity</CardTitle>
          <CardDescription>Your team's latest actions</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>User</TableHead>
                <TableHead>Action</TableHead>
                <TableHead>Time</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {recentActivity.map((activity) => (
                <TableRow key={activity.id}>
                  <TableCell className="font-medium">{activity.user}</TableCell>
                  <TableCell>{activity.action}</TableCell>
                  <TableCell>{activity.time}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
};

export default DashboardPage;