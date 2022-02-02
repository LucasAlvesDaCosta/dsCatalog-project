import ContentLoader from 'react-content-loader';

const CardLoader = () => (
  <div className="card-loader-container">
    <ContentLoader
      speed={1}
      width={300}
      height={300}
      viewBox="0 0 300 300"
      backgroundColor="#f5f0f0"
      foregroundColor="#d7d4f2"
    >
      <rect x="-4" y="23" rx="2" ry="2" width="279" height="20" />
      <rect x="0" y="60" rx="2" ry="2" width="400" height="400" />
    </ContentLoader>
  </div>
);

export default CardLoader;
