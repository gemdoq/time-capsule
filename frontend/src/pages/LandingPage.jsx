import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import Layout from '../components/Layout';

export default function LandingPage() {
  const { isAuthenticated } = useAuth();

  return (
    <Layout>
      {/* Hero Section */}
      <section className="bg-gradient-to-b from-primary-50 to-white py-20">
        <div className="max-w-6xl mx-auto px-4 text-center">
          <h1 className="text-5xl font-bold text-gray-900 mb-6">
            미래의 나를 증명하세요
          </h1>
          <p className="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
            당신의 예측을 기록하고, 원하는 시점에 공개하세요.
            <br />
            "내가 이미 알고 있었어"를 증명할 수 있습니다.
          </p>
          <Link
            to={isAuthenticated ? '/predictions/new' : '/signup'}
            className="inline-block bg-primary-600 text-white px-8 py-4 rounded-lg text-lg font-medium hover:bg-primary-700 transition"
          >
            시작하기
          </Link>
        </div>
      </section>

      {/* How it Works */}
      <section className="py-20">
        <div className="max-w-6xl mx-auto px-4">
          <h2 className="text-3xl font-bold text-center text-gray-900 mb-12">
            어떻게 작동하나요?
          </h2>
          <div className="grid md:grid-cols-3 gap-8">
            <div className="text-center p-6">
              <div className="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl">1</span>
              </div>
              <h3 className="text-xl font-semibold mb-2">예측 작성</h3>
              <p className="text-gray-600">
                미래에 일어날 일을 예측하고 기록하세요.
                작성 시점이 자동으로 저장됩니다.
              </p>
            </div>
            <div className="text-center p-6">
              <div className="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl">2</span>
              </div>
              <h3 className="text-xl font-semibold mb-2">공개일 설정</h3>
              <p className="text-gray-600">
                예측을 공개할 날짜를 지정하세요.
                그 전까지는 아무도 내용을 볼 수 없습니다.
              </p>
            </div>
            <div className="text-center p-6">
              <div className="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl">3</span>
              </div>
              <h3 className="text-xl font-semibold mb-2">예측 증명</h3>
              <p className="text-gray-600">
                공개일이 되면 예측 내용과 함께
                "미리 알고 있었다"는 증거가 공개됩니다.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Use Cases */}
      <section className="py-20 bg-gray-50">
        <div className="max-w-6xl mx-auto px-4">
          <h2 className="text-3xl font-bold text-center text-gray-900 mb-12">
            이런 상황에 사용하세요
          </h2>
          <div className="grid md:grid-cols-2 gap-6">
            <div className="bg-white p-6 rounded-lg shadow-sm">
              <h3 className="text-lg font-semibold mb-2">투자 예측</h3>
              <p className="text-gray-600">
                "이 주식이 오를 거야"라는 예측을 미리 기록해두세요.
              </p>
            </div>
            <div className="bg-white p-6 rounded-lg shadow-sm">
              <h3 className="text-lg font-semibold mb-2">스포츠 결과</h3>
              <p className="text-gray-600">
                경기 결과를 예측하고 친구들에게 자랑하세요.
              </p>
            </div>
            <div className="bg-white p-6 rounded-lg shadow-sm">
              <h3 className="text-lg font-semibold mb-2">인생 조언</h3>
              <p className="text-gray-600">
                지금 말하기 어려운 조언을 미래에 전달하세요.
              </p>
            </div>
            <div className="bg-white p-6 rounded-lg shadow-sm">
              <h3 className="text-lg font-semibold mb-2">기념일 메시지</h3>
              <p className="text-gray-600">
                특별한 날에 공개될 메시지를 미리 준비하세요.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-20">
        <div className="max-w-6xl mx-auto px-4 text-center">
          <h2 className="text-3xl font-bold text-gray-900 mb-4">
            지금 바로 시작하세요
          </h2>
          <p className="text-gray-600 mb-8">
            무료로 예측을 기록하고 미래에 증명하세요.
          </p>
          <Link
            to={isAuthenticated ? '/predictions/new' : '/signup'}
            className="inline-block bg-primary-600 text-white px-8 py-4 rounded-lg text-lg font-medium hover:bg-primary-700 transition"
          >
            무료로 시작하기
          </Link>
        </div>
      </section>
    </Layout>
  );
}
