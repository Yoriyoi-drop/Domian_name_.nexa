import React from 'react';
import { Button } from '@/shared/components/ui/button';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/shared/components/ui/card';
import { useParams } from 'react-router-dom';
import useApi from '@/shared/hooks/useApi';

const UserDetailPage = () => {
  const { id } = useParams();
  const { get } = useApi();
  
  // Mock data for now - in a real app, this would come from an API call
  const { data: user = {} } = get(`/api/v1/users/${id}`, {
    initialData: {
      id: 1,
      username: 'johndoe',
      email: 'john@example.com',
      role: 'Admin',
      status: 'Active',
      createdAt: '2023-01-15',
      lastLogin: '2023-11-20',
      profile: {
        firstName: 'John',
        lastName: 'Doe',
        phone: '+1 (555) 123-4567',
        address: '123 Main St, Anytown, USA'
      }
    }
  });

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">User Details</h1>
        <div className="flex space-x-2">
          <Button variant="outline">Edit Profile</Button>
          <Button variant="outline">Send Message</Button>
        </div>
      </div>
      
      <Card>
        <CardHeader>
          <CardTitle>User Information</CardTitle>
          <CardDescription>Details about the user account</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <h3 className="text-sm font-medium text-gray-500">Username</h3>
              <p className="text-lg">{user.username}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-500">Email</h3>
              <p className="text-lg">{user.email}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-500">Role</h3>
              <p className="text-lg">{user.role}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-500">Status</h3>
              <p className="text-lg">
                <span className={`px-2 py-1 rounded-full ${
                  user.status === 'Active' 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {user.status}
                </span>
              </p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-500">Created</h3>
              <p className="text-lg">{user.createdAt}</p>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-500">Last Login</h3>
              <p className="text-lg">{user.lastLogin}</p>
            </div>
          </div>
          
          <div className="mt-6">
            <h3 className="text-lg font-medium mb-2">Profile Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <h3 className="text-sm font-medium text-gray-500">First Name</h3>
                <p className="text-lg">{user.profile?.firstName}</p>
              </div>
              <div>
                <h3 className="text-sm font-medium text-gray-500">Last Name</h3>
                <p className="text-lg">{user.profile?.lastName}</p>
              </div>
              <div>
                <h3 className="text-sm font-medium text-gray-500">Phone</h3>
                <p className="text-lg">{user.profile?.phone}</p>
              </div>
              <div>
                <h3 className="text-sm font-medium text-gray-500">Address</h3>
                <p className="text-lg">{user.profile?.address}</p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default UserDetailPage;