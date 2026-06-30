import { useState } from 'react';

import { login, registerUser } from '@/api/auth';
import { extractErrorMessage } from '@/lib/error';
import { clearToken, clearUser, readToken, readUser, writeToken, writeUser } from '@/lib/storage';
import type { CurrentUser } from '@/types';

export function useAuth() {
  const [token, setToken] = useState<string | null>(readToken());
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(readUser<CurrentUser>());

  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [registerEmail, setRegisterEmail] = useState('');
  const [registerPassword, setRegisterPassword] = useState('');

  async function handleLoginSubmit(
    e: React.FormEvent<HTMLFormElement>,
    onError: (msg: string) => void,
    onClear: () => void,
  ) {
    e.preventDefault();
    onClear();
    try {
      const res = await login({ email: loginEmail, password: loginPassword });
      writeToken(res.token);
      writeUser({ email: res.email, roles: res.roles });
      setToken(res.token);
      setCurrentUser({ email: res.email, roles: res.roles });
    } catch (err) {
      onError(extractErrorMessage(err, 'Invalid credentials. Please check your email and password.'));
      console.error(err);
    }
  }

  async function handleRegisterSubmit(
    e: React.FormEvent<HTMLFormElement>,
    onError: (msg: string) => void,
    onMessage: (msg: string) => void,
    onClear: () => void,
  ) {
    e.preventDefault();
    onClear();
    try {
      await registerUser({ email: registerEmail, password: registerPassword });
      setRegisterEmail('');
      setRegisterPassword('');
      onMessage('User registered successfully. An administrator needs to activate your account before you can log in.');
    } catch (err) {
      onError(extractErrorMessage(err, 'Could not register. The email may already be in use.'));
      console.error(err);
    }
  }

  function handleLogout(onAfterLogout: () => void) {
    clearToken();
    clearUser();
    setToken(null);
    setCurrentUser(null);
    onAfterLogout();
  }

  return {
    token,
    currentUser,
    loginEmail,
    setLoginEmail,
    loginPassword,
    setLoginPassword,
    registerEmail,
    setRegisterEmail,
    registerPassword,
    setRegisterPassword,
    handleLoginSubmit,
    handleRegisterSubmit,
    handleLogout,
  };
}
