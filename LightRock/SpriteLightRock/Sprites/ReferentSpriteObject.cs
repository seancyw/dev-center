using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Shapes;
using System.Windows;
using System.Windows.Input;


namespace SpriteLightRock.Sprites
{
    class ReferentSpriteObject
    {
        class LocalDef
        {
            public const double NOT_SCALED_VALUE = 0;
            public const double DEFAULT_ANGLE = 0;
            public const double DEFAULT_ANCHOR_X = 0;
            public const double DEFAULT_ANCHOR_Y = 0;
            public const double DEFAULT_SCALE_X = NOT_SCALED_VALUE;
            public const double DEFAULT_SCALE_Y = NOT_SCALED_VALUE;
        }
        private double      _anchorX;
        private double      _anchorY;
        private double      _scaleX;
        private double      _scaleY;
        private double      _angle;
        private readonly object      _referenceObject;

        public object ReferenceObject
        {
            get { return _referenceObject; }
        }
        public double ScaleX
        {
            get { return _scaleX; }
            set { _scaleX = value; }
        }
        public double ScaleY
        {
            get { return _scaleY; }
            set { _scaleY = value; }
        }
        public double AnchorX
        {
            get { return _anchorX; }
            set { _anchorX = value; }
        }
        public double AnchorY
        {
            get { return _anchorY; }
            set { _anchorY = value; }
        }
        public double Angle
        {
            get { return _angle; }
            set {
                if (_angle >= 0 && _angle <= 2 * Math.PI)
                {
                    _angle = value;
                }
                else
                {
                    //Exeption Here
                }
            }
        }

        public ReferentSpriteObject(
            object referenceObject,
            double x,
            double y
        )
            :this(
                referenceObject,
                x,
                y,
                LocalDef.DEFAULT_ANCHOR_X,
                LocalDef.DEFAULT_ANCHOR_Y,
                LocalDef.DEFAULT_SCALE_X,
                LocalDef.DEFAULT_SCALE_Y,
                LocalDef.DEFAULT_ANGLE
            )
        {
        }

        public ReferentSpriteObject(            
            object referenceObject,
            double x,
            double y,
            double anchorX,
            double anchorY,
            double scaleX,
            double scaleY,
            double angle
        )           
        {
            _referenceObject = referenceObject;
            AnchorX = AnchorX;
            AnchorY = AnchorY;
            ScaleX = scaleX;
            ScaleY = scaleY;
            Angle = angle;
        }
        public virtual Rect GetBoundRect()
        {
            throw new NotImplementedException();
        }
    }
}
