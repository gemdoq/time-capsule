import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import Layout from '../components/Layout';
import Input from '../components/Input';
import Button from '../components/Button';

export default function SignupPage() {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    passwordConfirm: '',
    nickname: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { signup } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.passwordConfirm) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    if (formData.password.length < 8) {
      setError('비밀번호는 8자 이상이어야 합니다.');
      return;
    }

    setLoading(true);

    try {
      await signup(formData.email, formData.password, formData.nickname);
      navigate('/login', {
        state: { message: '회원가입이 완료되었습니다. 로그인해주세요.' },
      });
    } catch (err) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="min-h-[80vh] flex items-center justify-center py-12 px-4">
        <div className="max-w-md w-full">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-bold text-gray-900">회원가입</h1>
            <p className="text-gray-600 mt-2">
              이미 계정이 있으신가요?{' '}
              <Link to="/login" className="text-primary-600 hover:underline">
                로그인
              </Link>
            </p>
          </div>

          <form onSubmit={handleSubmit} className="bg-white p-8 rounded-lg shadow-sm">
            {error && (
              <div className="mb-4 p-3 bg-red-50 text-red-600 rounded-lg text-sm">
                {error}
              </div>
            )}

            <Input
              label="이메일"
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="이메일을 입력하세요"
              required
              className="mb-4"
            />

            <Input
              label="닉네임"
              type="text"
              name="nickname"
              value={formData.nickname}
              onChange={handleChange}
              placeholder="닉네임을 입력하세요"
              required
              className="mb-4"
            />

            <Input
              label="비밀번호"
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="8자 이상 입력하세요"
              required
              className="mb-4"
            />

            <Input
              label="비밀번호 확인"
              type="password"
              name="passwordConfirm"
              value={formData.passwordConfirm}
              onChange={handleChange}
              placeholder="비밀번호를 다시 입력하세요"
              required
              className="mb-6"
            />

            <Button type="submit" loading={loading} className="w-full">
              회원가입
            </Button>
          </form>
        </div>
      </div>
    </Layout>
  );
}
