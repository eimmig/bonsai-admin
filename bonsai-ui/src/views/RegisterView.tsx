import type React from 'react';
import { useState } from 'react';
import { AlertTriangle, CheckCircle2, Loader2 } from 'lucide-react';

interface RegisterViewProps {
  registerEmail: string;
  setRegisterEmail: (v: string) => void;
  registerPassword: string;
  setRegisterPassword: (v: string) => void;
  error: string | null;
  message: string | null;
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
  onGoToLogin: () => void;
}

export function RegisterView({
  registerEmail,
  setRegisterEmail,
  registerPassword,
  setRegisterPassword,
  error,
  message,
  onSubmit,
  onGoToLogin,
}: RegisterViewProps) {
  const [submitting, setSubmitting] = useState(false);

  function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    setSubmitting(true);
    try {
      onSubmit(e);
    } finally {
      setTimeout(() => setSubmitting(false), 400);
    }
  }

  return (
    <div
      className="flex min-h-screen items-center justify-center bg-bg"
      style={{
        background:
          'radial-gradient(ellipse at 20% 20%, rgba(109,184,90,0.12) 0%, transparent 50%), #0c1410',
      }}
    >
      <div className="flex w-full max-w-100 flex-col gap-6 rounded-3xl border border-white/8 bg-surface p-9">
        <div className="flex items-center gap-2.5 text-[1rem] font-semibold text-bright">
          <span className="h-2.5 w-2.5 shrink-0 rounded-full bg-accent" />
          <span>Bonsai Admin</span>
        </div>

        <div className="flex gap-6 border-b border-white/8">
          <button
            type="button"
            className="-mb-px cursor-pointer border-b-2 border-transparent px-0.5 pb-3 text-[0.95rem] font-medium text-soft transition-colors hover:text-main"
            onClick={onGoToLogin}
          >
            Log in
          </button>
          <span className="-mb-px border-b-2 border-accent px-0.5 pb-3 text-[0.95rem] font-semibold text-bright">
            Create account
          </span>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-3.5">
          <p className="-mt-2 text-[0.82rem] text-soft">
            After registering, an administrator will need to activate your account before you can log in.
          </p>

          <label className="form-field">
            <span className="form-label">Email</span>
            <input
              type="email"
              className="form-input"
              value={registerEmail}
              onChange={(e) => setRegisterEmail(e.target.value)}
              autoComplete="email"
              required
            />
          </label>

          <label className="form-field">
            <span className="form-label">Password</span>
            <input
              type="password"
              className="form-input"
              value={registerPassword}
              onChange={(e) => setRegisterPassword(e.target.value)}
              autoComplete="new-password"
              required
            />
          </label>

          {error ? (
            <p className="flex items-center gap-2 rounded-[10px] bg-danger/12 px-3 py-2.5 text-[0.85rem] text-[#fca5a5]">
              <AlertTriangle size={15} className="shrink-0" />
              {error}
            </p>
          ) : null}
          {message ? (
            <p className="flex items-center gap-2 rounded-[10px] bg-accent/10 px-3 py-2.5 text-[0.85rem] text-[#86efac]">
              <CheckCircle2 size={15} className="shrink-0" />
              {message}
            </p>
          ) : null}

          <button type="submit" className="btn-primary mt-1 flex w-full items-center justify-center gap-2" disabled={submitting}>
            {submitting && <Loader2 size={15} className="animate-spin" />}
            Register
          </button>
        </form>

        <p className="text-center text-[0.85rem] text-soft">
          Already have an account?{' '}
          <button type="button" className="btn-link" onClick={onGoToLogin}>
            Log in
          </button>
        </p>
      </div>
    </div>
  );
}
