import React, { useState } from 'react';
import { Button } from '../../shared/components/ui/button';
import { Input } from '../../shared/components/ui/input';
import { Table, TableHeader, TableBody, TableRow, TableCell, TableHead } from '../../shared/components/ui/table';
import { useApi } from '../../shared/hooks/useApi';
import { Link } from 'react-router-dom';

const UsersListPage = () => {
  const { get } = useApi();
  const [searchTerm, setSearchTerm] = useState('');
  
  // Mock data for now - in a real app, this would come from an API call
  const { data: users = [], isLoading } = get('/api/v1/users', {
    initialData: [
      { id: 1, username: 'johndoe', email: 'john@example.com', role: 'Admin', status: 'Active', createdAt: '2023-01-15' },
      { id: 2, username: 'janedoe', email: 'jane@example.com', role: 'User', status: 'Active', createdAt: '2023-02-20' },
      { id: 3, username: 'bobsmith', email: 'bob@example.com', role: 'Moderator', status: 'Inactive', createdAt: '2023-03-10' },
      { id: 4, username: 'alicew', email: 'alice@example.com', role: 'User', status: 'Active', createdAt: '2023-04-05' },
    ]
  });

  const filteredUsers = users.filter(user => 
    user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Users</h1>
        <Button asChild>
          <Link to="/users/new">Add New User</Link>
        </Button>
      </div>
      
      <div className="flex space-x-4 mb-6">
        <Input
          placeholder="Search users..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="max-w-sm"
        />
      </div>
      
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>ID</TableHead>
            <TableHead>Username</TableHead>
            <TableHead>Email</TableHead>
            <TableHead>Role</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Created</TableHead>
            <TableHead>Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {filteredUsers.map((user) => (
            <TableRow key={user.id}>
              <TableCell>{user.id}</TableCell>
              <TableCell>{user.username}</TableCell>
              <TableCell>{user.email}</TableCell>
              <TableCell>{user.role}</TableCell>
              <TableCell>
                <span className={`px-2 py-1 rounded-full text-xs ${
                  user.status === 'Active' 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {user.status}
                </span>
              </TableCell>
              <TableCell>{user.createdAt}</TableCell>
              <TableCell>
                <div className="flex space-x-2">
                  <Button variant="outline" size="sm" asChild>
                    <Link to={`/users/${user.id}`}>View</Link>
                  </Button>
                  <Button variant="outline" size="sm" asChild>
                    <Link to={`/users/${user.id}/edit`}>Edit</Link>
                  </Button>
                </div>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
};

export default UsersListPage;