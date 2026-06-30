import { useEffect } from 'react';
import { AlertTriangle, CheckCircle2, X } from 'lucide-react';

export interface ToastItem {
  id: number;
  type: 'error' | 'success';
  text: string;
}

interface ToastProps {
  toast: ToastItem;
  onDismiss: (id: number) => void;
}

function Toast({ toast, onDismiss }: ToastProps) {
  useEffect(() => {
    if (toast.type !== 'success') return;
    const timer = setTimeout(() => onDismiss(toast.id), 4000);
    return () => clearTimeout(timer);
  }, [toast.id, toast.type, onDismiss]);

  const isError = toast.type === 'error';

  return (
    <div
      className={`flex w-80 items-start gap-2.5 rounded-xl border px-4 py-3 text-[0.88rem] shadow-[0_8px_24px_rgba(0,0,0,0.4)] ${
        isError
          ? 'border-danger/25 bg-[#1a1010] text-[#fca5a5]'
          : 'border-accent/25 bg-[#0f1a12] text-[#86efac]'
      }`}
    >
      {isError ? (
        <AlertTriangle size={16} className="mt-0.5 shrink-0" />
      ) : (
        <CheckCircle2 size={16} className="mt-0.5 shrink-0" />
      )}
      <span className="flex-1">{toast.text}</span>
      <button
        type="button"
        className="shrink-0 cursor-pointer text-current/70 hover:text-current"
        onClick={() => onDismiss(toast.id)}
        aria-label="Close"
      >
        <X size={14} />
      </button>
    </div>
  );
}

interface ToastContainerProps {
  toasts: ToastItem[];
  onDismiss: (id: number) => void;
}

export function ToastContainer({ toasts, onDismiss }: ToastContainerProps) {
  if (toasts.length === 0) return null;

  return (
    <div className="fixed bottom-4 right-4 z-[200] flex flex-col gap-2.5">
      {toasts.map((t) => (
        <Toast key={t.id} toast={t} onDismiss={onDismiss} />
      ))}
    </div>
  );
}
