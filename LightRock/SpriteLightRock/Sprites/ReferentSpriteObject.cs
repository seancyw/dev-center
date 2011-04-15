using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Shapes;
using System.Windows;
using System.Windows.Input;
using System.Windows.Media;


namespace SpriteLightRock.Sprites
{
    class ReferentSpriteObject
    {
        class LocalDef
        {
            public const double NOT_SCALED_VALUE = 0;
        }
        private object             _referenceObject;
        private Matrix             _matrix;

        public object ReferenceObject
        {
            get { return _referenceObject; }
            private set { _referenceObject = value; }
        }
        
        public Matrix Transform
        {
            get
            {
                return _matrix;
            }
            set
            {
                _matrix = value;
            }
        }
        public ReferentSpriteObject(
            object referenceObject,
            double x,
            double y
        )   
        {
            ReferenceObject     = referenceObject;
            if (Transform == null)
            {
                Transform = new Matrix();
                Transform.Translate(x, y);
            }
        }

        public ReferentSpriteObject(            
            object referenceObject,            
            Matrix matrix
        )           
        {
            ReferenceObject     = referenceObject;
            Transform           = matrix;
        }
        public virtual Rect GetBoundRect()
        {
            throw new NotImplementedException();
        }
    }
}
