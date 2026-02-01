import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import Layout from '../components/Layout';
import Input from '../components/Input';
import Button from '../components/Button';
import { userApi } from '../api/auth';

export default function MyPage() {
  const { user, updateUser } = useAuth();
  const [activeTab, setActiveTab] = useState('profile');

  return (
    <Layout>
      <div className="max-w-2xl mx-auto px-4 py-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-8">마이페이지</h1>

        {/* Tabs */}
        <div className="flex border-b mb-6">
          <button
            onClick={() => setActiveTab('profile')}
            className={`px-4 py-2 font-medium border-b-2 transition ${
              activeTab === 'profile'
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            프로필 수정
          </button>
          <button
            onClick={() => setActiveTab('password')}
            className={`px-4 py-2 font-medium border-b-2 transition ${
              activeTab === 'password'
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            비밀번호 변경
          </button>
        </div>

        {activeTab === 'profile' ? (
          <ProfileForm user={user} updateUser={updateUser} />
        ) : (
          <PasswordForm />
        )}
      </div>
    </Layout>
  );
}

function ProfileForm({ user, updateUser }) {
  const [nickname, setNickname] = useState(user?.nickname || '');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      const response = await userApi.updateProfile({ nickname });
      updateUser(response.data);
      setMessage({ type: 'success', text: '프로필이 수정되었습니다.' });
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || '프로필 수정에 실패했습니다.',
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-sm">
      {message.text && (
        <div
          className={`mb-4 p-3 rounded-lg text-sm ${
            message.type === 'success'
              ? 'bg-green-50 text-green-600'
              : 'bg-red-50 text-red-600'
          }`}
        >
          {message.text}
        </div>
      )}

      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-1">
          이메일
        </label>
        <input
          type="email"
          value={user?.email || ''}
          disabled
          className="w-full px-4 py-2 border border-gray-200 rounded-lg bg-gray-50 text-gray-500"
        />
      </div>

      <Input
        label="닉네임"
        value={nickname}
        onChange={(e) => setNickname(e.target.value)}
        required
        className="mb-6"
      />

      <Button type="submit" loading={loading}>
        저장
      </Button>
    </form>
  );
}

function PasswordForm() {
  const [formData, setFormData] = useState({
    currentPassword: '',
    newPassword: '',
    newPasswordConfirm: '',
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });

    if (formData.newPassword !== formData.newPasswordConfirm) {
      setMessage({ type: 'error', text: '새 비밀번호가 일치하지 않습니다.' });
      return;
    }

    if (formData.newPassword.length < 8) {
      setMessage({ type: 'error', text: '비밀번호는 8자 이상이어야 합니다.' });
      return;
    }

    setLoading(true);

    try {
      await userApi.changePassword({
        currentPassword: formData.currentPassword,
        newPassword: formData.newPassword,
      });
      setMessage({ type: 'success', text: '비밀번호가 변경되었습니다.' });
      setFormData({
        currentPassword: '',
        newPassword: '',
        newPasswordConfirm: '',
      });
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || '비밀번호 변경에 실패했습니다.',
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-sm">
      {message.text && (
        <div
          className={`mb-4 p-3 rounded-lg text-sm ${
            message.type === 'success'
              ? 'bg-green-50 text-green-600'
              : 'bg-red-50 text-red-600'
          }`}
        >
          {message.text}
        </div>
      )}

      <Input
        label="현재 비밀번호"
        type="password"
        name="currentPassword"
        value={formData.currentPassword}
        onChange={handleChange}
        required
        className="mb-4"
      />

      <Input
        label="새 비밀번호"
        type="password"
        name="newPassword"
        value={formData.newPassword}
        onChange={handleChange}
        placeholder="8자 이상"
        required
        className="mb-4"
      />

      <Input
        label="새 비밀번호 확인"
        type="password"
        name="newPasswordConfirm"
        value={formData.newPasswordConfirm}
        onChange={handleChange}
        required
        className="mb-6"
      />

      <Button type="submit" loading={loading}>
        비밀번호 변경
      </Button>
    </form>
  );
}
