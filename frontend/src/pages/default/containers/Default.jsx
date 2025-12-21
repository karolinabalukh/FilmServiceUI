import { useIntl } from 'react-intl';
import React from 'react';
import { useNavigate } from 'react-router-dom';
import Typography from 'components/Typography';
import Button from 'components/Button';

function Default() {
  const { formatMessage } = useIntl();
  const navigate = useNavigate();

  return (
      <div>

        <Button
            style={{ marginTop: '24px' }}
            onClick={() => navigate('/films')}
        >
          Перейти до списку фільмів 🎬
        </Button>
      </div>
  );
}

export default Default;
