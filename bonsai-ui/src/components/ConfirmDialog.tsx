import { createRoot } from 'react-dom/client';

interface ConfirmModalProps {
  title: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
}

function ConfirmModal({
  title,
  message,
  confirmLabel = 'Confirm',
  cancelLabel = 'Cancel',
  onConfirm,
  onCancel,
}: ConfirmModalProps) {
  return (
    <div
      className="fixed inset-0 z-[500] flex items-center justify-center bg-black/60 backdrop-blur-sm"
      onClick={(e) => { if (e.target === e.currentTarget) onCancel(); }}
    >
      <div className="flex w-full max-w-sm flex-col gap-5 rounded-2xl border border-white/10 bg-surface p-6 shadow-[0_24px_64px_rgba(0,0,0,0.6)]">
        <div>
          <h2 className="text-[1rem] font-bold text-bright">{title}</h2>
          <p className="mt-1.5 text-[0.88rem] text-soft">{message}</p>
        </div>
        <div className="flex justify-end gap-2.5">
          <button type="button" className="btn-secondary px-4 py-2" onClick={onCancel}>
            {cancelLabel}
          </button>
          <button type="button" className="btn-primary px-4 py-2" onClick={onConfirm}>
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}

export function confirm(title: string, message: string): Promise<boolean> {
  return new Promise((resolve) => {
    const container = document.createElement('div');
    document.body.appendChild(container);
    const root = createRoot(container);

    function cleanup(result: boolean) {
      root.unmount();
      document.body.removeChild(container);
      resolve(result);
    }

    root.render(
      <ConfirmModal
        title={title}
        message={message}
        onConfirm={() => cleanup(true)}
        onCancel={() => cleanup(false)}
      />,
    );
  });
}
