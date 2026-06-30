import { useEffect, useRef, useState } from 'react';
import { FileText, FolderKanban, LogOut, RefreshCw, Upload, Users } from 'lucide-react';

import { listOrders, getOrderSummary } from '@/api/orders';
import { listUsers } from '@/api/users';
import { ToastContainer, type ToastItem } from '@/components/Toast';
import { useAuth } from '@/hooks/useAuth';
import { useAttachments } from '@/hooks/useAttachments';
import { useOrders } from '@/hooks/useOrders';
import { useUsers } from '@/hooks/useUsers';
import { extractErrorMessage } from '@/lib/error';
import { FilesView } from '@/views/FilesView';
import { LoginView } from '@/views/LoginView';
import { OrdersView } from '@/views/OrdersView';
import { OverviewView } from '@/views/OverviewView';
import { RegisterView } from '@/views/RegisterView';
import { UsersView } from '@/views/UsersView';
import type { DashboardSnapshot } from '@/types';

type AuthScreen = 'login' | 'register';

type ViewKey = 'overview' | 'users' | 'orders' | 'files';

const viewTitles: Record<ViewKey, string> = {
  overview: 'Overview',
  users: 'Users',
  orders: 'Orders',
  files: 'Attachments',
};

const navItems: { key: ViewKey; label: string; icon: typeof FolderKanban }[] = [
  { key: 'overview', label: 'Overview', icon: FolderKanban },
  { key: 'users', label: 'Users', icon: Users },
  { key: 'orders', label: 'Orders', icon: FileText },
  { key: 'files', label: 'Attachments', icon: Upload },
];

function App() {
  const [activeView, setActiveView] = useState<ViewKey>('overview');
  const [authScreen, setAuthScreen] = useState<AuthScreen>('login');
  const [loading, setLoading] = useState(false);
  const [toasts, setToasts] = useState<ToastItem[]>([]);
  const toastIdRef = useRef(0);
  const [dashboard, setDashboard] = useState<DashboardSnapshot>({
    users: null,
    orders: null,
    orderSummary: null,
  });

  function addToast(type: ToastItem['type'], text: string) {
    const id = ++toastIdRef.current;
    setToasts((cur) => [...cur, { id, type, text }]);
  }

  function dismissToast(id: number) {
    setToasts((cur) => cur.filter((t) => t.id !== id));
  }

  const onError = (msg: string) => addToast('error', msg);
  const onMessage = (msg: string) => addToast('success', msg);

  const orders = useOrders(dashboard.orders, onError, onMessage, () => void refreshDashboard());

  const auth = useAuth();

  const users = useUsers(dashboard.users, onError, onMessage, () => void refreshDashboard());

  const attachments = useAttachments(orders.selectedOrderId, auth.token, onError, onMessage);

  useEffect(() => {
    if (!auth.token) {
      setDashboard({ users: null, orders: null, orderSummary: null });
      return;
    }
    void refreshDashboard();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [auth.token]);

  async function refreshDashboard() {
    setLoading(true);
    try {
      const [usersPage, ordersPage, summary] = await Promise.all([
        listUsers(),
        listOrders(orders.orderFilters),
        getOrderSummary(),
      ]);
      setDashboard({ users: usersPage, orders: ordersPage, orderSummary: summary });
    } catch (err) {
      onError(extractErrorMessage(err, 'Could not load the data. Please check that the API is running.'));
      console.error(err);
    } finally {
      setLoading(false);
    }
  }

  function navigate(view: ViewKey) {
    setActiveView(view);
  }

  function handleLogout() {
    auth.handleLogout(() => {
      setDashboard({ users: null, orders: null, orderSummary: null });
      setToasts([]);
    });
  }

  // ── Auth screens ──────────────────────────────────────────────────────
  if (!auth.token) {
    const authError = toasts.find((t) => t.type === 'error')?.text ?? null;
    const authMessage = toasts.find((t) => t.type === 'success')?.text ?? null;

    if (authScreen === 'register') {
      return (
        <RegisterView
          registerEmail={auth.registerEmail}
          setRegisterEmail={auth.setRegisterEmail}
          registerPassword={auth.registerPassword}
          setRegisterPassword={auth.setRegisterPassword}
          error={authError}
          message={authMessage}
          onSubmit={(e) =>
            void auth.handleRegisterSubmit(
              e,
              onError,
              onMessage,
              () => setToasts([]),
            )
          }
          onGoToLogin={() => { setToasts([]); setAuthScreen('login'); }}
        />
      );
    }

    return (
      <LoginView
        loginEmail={auth.loginEmail}
        setLoginEmail={auth.setLoginEmail}
        loginPassword={auth.loginPassword}
        setLoginPassword={auth.setLoginPassword}
        error={authError}
        onSubmit={(e) =>
          void auth.handleLoginSubmit(e, onError, () => setToasts([]))
        }
        onGoToRegister={() => { setToasts([]); setAuthScreen('register'); }}
      />
    );
  }

  // ── Main panel ───────────────────────────────────────────────────────────
  return (
    <div className="flex h-screen overflow-hidden bg-bg">
      {/* Sidebar */}
      <aside className="flex w-55 shrink-0 flex-col gap-1 overflow-y-auto border-r border-white/8 bg-surface p-3 max-[640px]:w-16 max-[900px]:w-45">
        <div className="mb-2 flex items-center gap-2.5 border-b border-white/8 px-2 py-2 pb-4">
          <span className="h-2.5 w-2.5 shrink-0 rounded-full bg-accent" />
          <div className="overflow-hidden max-[640px]:hidden">
            <p className="truncate text-[0.92rem] font-bold text-bright">Bonsai Admin</p>
            <p className="max-w-35 truncate text-[0.75rem] text-soft">{auth.currentUser?.email}</p>
          </div>
        </div>

        <nav className="flex flex-1 flex-col gap-1">
          {navItems.map(({ key, label, icon: Icon }) => (
            <button
              key={key}
              type="button"
              className={`flex w-full items-center gap-2.5 rounded-[10px] px-3 py-2.5 text-left text-[0.88rem] font-medium transition-colors max-[640px]:justify-center ${
                activeView === key
                  ? 'border border-accent/25 bg-accent/18 text-[#c8f0b8]'
                  : 'border border-transparent text-soft hover:bg-accent/15 hover:text-main'
              }`}
              onClick={() => navigate(key)}
            >
              <Icon size={18} />
              <span className="max-[640px]:hidden">{label}</span>
            </button>
          ))}
        </nav>

        <button
          type="button"
          className="flex w-full items-center gap-2 rounded-[10px] px-3 py-2.5 text-left text-[0.85rem] text-soft transition-colors hover:bg-danger/12 hover:text-[#fca5a5] max-[640px]:justify-center"
          onClick={handleLogout}
        >
          <LogOut size={16} />
          <span className="max-[640px]:hidden">Log out</span>
        </button>
      </aside>

      {/* Main content */}
      <main className="flex flex-1 flex-col overflow-hidden">
        {/* Topbar */}
        <header className="flex shrink-0 items-center justify-between border-b border-white/8 bg-surface px-6 py-4 max-[640px]:px-4 max-[640px]:py-3">
          <div className="flex items-center gap-3">
            <h1 className="text-[1.1rem] font-bold text-bright">{viewTitles[activeView]}</h1>
            {loading && (
              <span className="rounded-full bg-accent/15 px-2.5 py-0.5 text-[0.75rem] text-accent">
                Refreshing...
              </span>
            )}
          </div>
          <button
            type="button"
            className="btn-secondary flex items-center gap-2"
            onClick={() => void refreshDashboard()}
          >
            <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
            Reload
          </button>
        </header>

        {/* Content area */}
        <div className="flex flex-1 flex-col gap-5 overflow-y-auto p-6 max-[640px]:p-4">
          {activeView === 'overview' && <OverviewView dashboard={dashboard} loading={loading} />}

          {activeView === 'users' && (
            <UsersView
              users={users.users}
              page={users.page}
              totalPages={users.totalPages}
              totalElements={users.totalElements}
              onPageChange={users.handlePageChange}
              searchTerm={users.searchTerm}
              setSearchTerm={users.setSearchTerm}
              selectedUserId={users.selectedUserId}
              setSelectedUserId={users.setSelectedUserId}
              selectedUserRoles={users.selectedUserRoles}
              selectedUser={users.selectedUser}
              toggleRole={users.toggleRole}
              handleActivateUser={users.handleActivateUser}
              handleAssignRoles={users.handleAssignRoles}
            />
          )}

          {activeView === 'orders' && (
            <OrdersView
              orders={orders.orders}
              page={orders.page}
              totalPages={orders.totalPages}
              totalElements={orders.totalElements}
              onPageChange={orders.handlePageChange}
              orderFilters={orders.orderFilters}
              setOrderFilters={orders.setOrderFilters}
              filterCustomerSearch={orders.filterCustomerSearch}
              setFilterCustomerSearch={orders.setFilterCustomerSearch}
              customerOptions={orders.customerOptions}
              selectedOrderId={orders.selectedOrderId}
              setSelectedOrderId={orders.setSelectedOrderId}
              selectedOrderStatus={orders.selectedOrderStatus}
              setSelectedOrderStatus={orders.setSelectedOrderStatus}
              selectedHistory={orders.selectedHistory}
              handleApplyOrderFilters={orders.handleApplyOrderFilters}
              handleResetOrderFilters={orders.handleResetOrderFilters}
              handleUpdateOrderStatus={orders.handleUpdateOrderStatus}
              handleLoadHistory={orders.handleLoadHistory}
              onRefresh={() => void refreshDashboard()}
            />
          )}

          {activeView === 'files' && (
            <FilesView
              orders={orders.orders}
              allOrders={orders.allOrders}
              selectedOrderId={orders.selectedOrderId}
              setSelectedOrderId={orders.setSelectedOrderId}
              setSelectedOrderStatus={orders.setSelectedOrderStatus}
              selectedOrder={orders.selectedOrder}
              orderAttachments={attachments.orderAttachments}
              attachmentType={attachments.attachmentType}
              setAttachmentType={attachments.setAttachmentType}
              attachmentFile={attachments.attachmentFile}
              setAttachmentFile={attachments.setAttachmentFile}
              handleUploadAttachment={attachments.handleUploadAttachment}
              handleDownloadAttachment={attachments.handleDownloadAttachment}
            />
          )}
        </div>
      </main>

      <ToastContainer toasts={toasts} onDismiss={dismissToast} />
    </div>
  );
}

export default App;
