import { useEffect, useMemo, useState } from 'react';

import { activateUser, assignRoles, listUsers } from '@/api/users';
import { confirm } from '@/components/ConfirmDialog';
import { extractErrorMessage } from '@/lib/error';
import type { PageResponse, UserResponse } from '@/types';

export function useUsers(
  usersPage: PageResponse<UserResponse> | null,
  onError: (msg: string) => void,
  onMessage: (msg: string) => void,
  onRefresh: () => void,
) {
  const [page, setPage] = useState(0);
  const [pagedUsers, setPagedUsers] = useState<PageResponse<UserResponse> | null>(usersPage);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    setPagedUsers(usersPage);
    setPage(0);
  }, [usersPage]);

  const allUsers = pagedUsers?.content ?? [];
  const users = useMemo(() => {
    if (!searchTerm.trim()) return allUsers;
    const term = searchTerm.trim().toLowerCase();
    return allUsers.filter((u) => u.email.toLowerCase().includes(term));
  }, [allUsers, searchTerm]);

  const [selectedUserId, setSelectedUserId] = useState('');
  const [selectedUserRoles, setSelectedUserRoles] = useState<string[]>(['OPERATOR']);

  const selectedUser = useMemo(
    () => allUsers.find((u) => u.id === selectedUserId) ?? null,
    [selectedUserId, allUsers],
  );

  useEffect(() => {
    if (allUsers.length > 0 && !selectedUserId) {
      setSelectedUserId(allUsers[0].id);
      setSelectedUserRoles(allUsers[0].roles.length > 0 ? allUsers[0].roles : ['OPERATOR']);
    }
  }, [selectedUserId, allUsers]);

  function toggleRole(role: string) {
    setSelectedUserRoles((cur) =>
      cur.includes(role) ? cur.filter((r) => r !== role) : [...cur, role],
    );
  }

  async function handlePageChange(newPage: number) {
    try {
      const data = await listUsers(newPage);
      setPagedUsers(data);
      setPage(newPage);
    } catch (err) {
      onError(extractErrorMessage(err, 'Could not load the users page.'));
      console.error(err);
    }
  }

  async function handleActivateUser(userId: string) {
    if (!await confirm('Activate user', 'Are you sure you want to activate this user?')) return;
    try {
      await activateUser(userId);
      onMessage('User activated.');
      onRefresh();
    } catch (err) {
      onError(extractErrorMessage(err, 'Could not activate the user.'));
      console.error(err);
    }
  }

  async function handleAssignRoles(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (!selectedUserId) {
      onError('Select a user.');
      return;
    }
    if (!await confirm('Update roles', "Confirm updating this user's roles?")) return;
    try {
      await assignRoles(selectedUserId, selectedUserRoles);
      onMessage('Roles updated.');
      onRefresh();
    } catch (err) {
      onError(extractErrorMessage(err, 'Could not update the roles.'));
      console.error(err);
    }
  }

  return {
    users,
    page,
    totalPages: pagedUsers?.totalPages ?? 1,
    totalElements: pagedUsers?.totalElements ?? 0,
    handlePageChange,
    searchTerm,
    setSearchTerm,
    selectedUserId,
    setSelectedUserId,
    selectedUserRoles,
    setSelectedUserRoles,
    selectedUser,
    toggleRole,
    handleActivateUser,
    handleAssignRoles,
  };
}

export { listUsers };
