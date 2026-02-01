import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import Input from '../components/Input';
import Button from '../components/Button';
import { predictionApi, attachmentApi } from '../api/predictions';

export default function CreatePredictionPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    releaseDate: '',
    recipientType: 'LINK_ONLY',
    recipientEmail: '',
  });
  const [files, setFiles] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleFileChange = (e) => {
    const newFiles = Array.from(e.target.files);
    setFiles([...files, ...newFiles]);
  };

  const removeFile = (index) => {
    setFiles(files.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // 예측 생성
      const predictionData = {
        title: formData.title,
        content: formData.content,
        releaseDate: formData.releaseDate,
        recipient: {
          type: formData.recipientType,
          email: formData.recipientType === 'EMAIL' ? formData.recipientEmail : null,
        },
      };

      const response = await predictionApi.create(predictionData);
      const predictionId = response.data.id;

      // 파일 업로드
      for (const file of files) {
        await attachmentApi.upload(predictionId, file);
      }

      navigate(`/predictions/${predictionId}`, {
        state: { message: '예측이 성공적으로 생성되었습니다!' },
      });
    } catch (err) {
      setError(err.response?.data?.message || '예측 생성에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 내일 날짜를 최소 날짜로 설정
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const minDate = tomorrow.toISOString().split('T')[0];

  return (
    <Layout>
      <div className="max-w-2xl mx-auto px-4 py-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-8">새 예측 작성</h1>

        <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-sm">
          {error && (
            <div className="mb-4 p-3 bg-red-50 text-red-600 rounded-lg text-sm">
              {error}
            </div>
          )}

          <Input
            label="제목"
            name="title"
            value={formData.title}
            onChange={handleChange}
            placeholder="예측의 제목을 입력하세요"
            required
            className="mb-4"
          />

          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              예측 내용
            </label>
            <textarea
              name="content"
              value={formData.content}
              onChange={handleChange}
              placeholder="예측 내용을 입력하세요..."
              required
              rows={6}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none resize-none"
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              첨부파일
            </label>
            <input
              type="file"
              onChange={handleFileChange}
              multiple
              className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-primary-50 file:text-primary-700 hover:file:bg-primary-100"
            />
            {files.length > 0 && (
              <ul className="mt-2 space-y-1">
                {files.map((file, index) => (
                  <li
                    key={index}
                    className="flex items-center justify-between text-sm text-gray-600 bg-gray-50 px-3 py-2 rounded"
                  >
                    <span>{file.name}</span>
                    <button
                      type="button"
                      onClick={() => removeFile(index)}
                      className="text-red-500 hover:text-red-700"
                    >
                      삭제
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </div>

          <Input
            label="공개일"
            type="date"
            name="releaseDate"
            value={formData.releaseDate}
            onChange={handleChange}
            min={minDate}
            required
            className="mb-4"
          />

          <div className="mb-6">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              수신자 지정
            </label>
            <div className="space-y-2">
              <label className="flex items-center">
                <input
                  type="radio"
                  name="recipientType"
                  value="LINK_ONLY"
                  checked={formData.recipientType === 'LINK_ONLY'}
                  onChange={handleChange}
                  className="mr-2"
                />
                <span>링크만 생성 (나중에 직접 전달)</span>
              </label>
              <label className="flex items-center">
                <input
                  type="radio"
                  name="recipientType"
                  value="EMAIL"
                  checked={formData.recipientType === 'EMAIL'}
                  onChange={handleChange}
                  className="mr-2"
                />
                <span>이메일로 지정</span>
              </label>
            </div>

            {formData.recipientType === 'EMAIL' && (
              <Input
                type="email"
                name="recipientEmail"
                value={formData.recipientEmail}
                onChange={handleChange}
                placeholder="수신자 이메일 입력"
                required
                className="mt-3"
              />
            )}
          </div>

          <div className="flex gap-3">
            <Button
              type="button"
              variant="secondary"
              onClick={() => navigate(-1)}
              className="flex-1"
            >
              취소
            </Button>
            <Button type="submit" loading={loading} className="flex-1">
              예측 생성
            </Button>
          </div>
        </form>
      </div>
    </Layout>
  );
}
