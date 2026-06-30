import { Search } from 'lucide-react';

import { CustomSelect } from '@/components/CustomSelect';
import { EmptyState } from '@/components/EmptyState';
import { Pagination } from '@/components/Pagination';
import { roleLabel } from '@/lib/format';
import type { UserResponse } from '@/types';

const roleOptions = ['ADMIN', 'OPERATOR'];

interface UsersViewProps {
  users: UserResponse[];
  page: number;
  totalPages: number;
  totalElements: number;
  onPageChange: (page: number) => void;
  searchTerm: string;
  setSearchTerm: (v: string) => void;
  selectedUserId: string;
  setSelectedUserId: (id: string) => void;
  selectedUserRoles: string[];
  selectedUser: UserResponse | null;
  toggleRole: (role: string) => void;
  handleActivateUser: (userId: string) => void;
  handleAssignRoles: (e: React.FormEvent<HTMLFormElement>) => void;
}

export function UsersView({
  users,
  page,
  totalPages,
  totalElements,
  onPageChange,
  searchTerm,
  setSearchTerm,
  selectedUserId,
  setSelectedUserId,
  selectedUserRoles,
  selectedUser,
  toggleRole,
  handleActivateUser,
  handleAssignRoles,
}: UsersViewProps) {
  return (
    <div className="grid grid-cols-2 items-start gap-5 max-[900px]:grid-cols-1">
      <div className="card">
        <h2 className="card-title">User list</h2>

        <label className="form-field">
          <div className="relative">
            <Search size={14} className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-dim" />
            <input
              className="form-input pl-8"
              placeholder="Search by email..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </label>

        {users.length === 0 ? (
          <EmptyState message="No users found." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full border-collapse">
              <thead>
                <tr>
                  <th className="table-th">Email</th>
                  <th className="table-th">Status</th>
                  <th className="table-th">Roles</th>
                  <th className="table-th">Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user: UserResponse) => (
                  <tr key={user.id}>
                    <td className="table-td">{user.email}</td>
                    <td className="table-td">
                      <span className={`badge ${user.active ? 'badge-green' : 'badge-gray'}`}>
                        {user.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="table-td">
                      {user.roles.map(roleLabel).join(', ') || '—'}
                    </td>
                    <td className="table-td">
                      <div className="flex items-center gap-1">
                        {!user.active && (
                          <button
                            type="button"
                            className="btn-link"
                            onClick={() => void handleActivateUser(user.id)}
                          >
                            Activate
                          </button>
                        )}
                        <button
                          type="button"
                          className="btn-link"
                          onClick={() => setSelectedUserId(user.id)}
                        >
                          Edit roles
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <Pagination
          page={page}
          totalPages={totalPages}
          totalElements={totalElements}
          onPageChange={onPageChange}
        />
      </div>

      <div className="card">
        <h2 className="card-title">Edit roles</h2>
        <form onSubmit={handleAssignRoles} className="flex flex-col gap-3.5">
          <div className="form-field">
            <span className="form-label">User</span>
            <CustomSelect
              value={selectedUserId}
              onChange={setSelectedUserId}
              searchable
              options={users.map((u) => ({
                value: u.id,
                label: u.email,
                sublabel: u.roles.map(roleLabel).join(', ') || 'No roles',
              }))}
            />
          </div>

          <fieldset className="rounded-[10px] border border-white/8 p-3">
            <legend className="px-1.5 text-[0.78rem] text-soft">Roles</legend>
            <div className="mt-2.5 flex flex-col gap-2">
              {roleOptions.map((role) => (
                <label key={role} className="flex cursor-pointer items-center gap-2 text-[0.88rem]">
                  <input
                    type="checkbox"
                    checked={selectedUserRoles.includes(role)}
                    onChange={() => toggleRole(role)}
                  />
                  {roleLabel(role)}
                </label>
              ))}
            </div>
          </fieldset>

          <button type="submit" className="btn-primary" disabled={!selectedUserId}>
            Save roles
          </button>
        </form>

        {selectedUser && (
          <div className="preview-box">
            <p className="preview-label">Selected user</p>
            <strong className="text-[0.9rem] text-main">{selectedUser.email}</strong>
            <span className="text-[0.82rem] text-soft">
              {selectedUser.roles.map(roleLabel).join(', ') || 'No roles'}
            </span>
          </div>
        )}
      </div>
    </div>
  );
}
