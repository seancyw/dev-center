using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Shapes;
using System.Windows.Media;
using System.Windows;

namespace SpriteLightRock.Sprites
{
    class FModule : ReferentSpriteObject
    {
        public FModule(
            object referenceObject,
            double x,
            double y
        )
            : base(referenceObject, x, y)
        {
        }        
        public Matrix GetTransformMatrix()
        {
            Matrix matrix = new Matrix();
            matrix.Scale(ScaleX, ScaleY);
            matrix.Rotate(Angle);
            return matrix;
        }
        public Rect GetRectBound()
        {
            Module module = ReferenceObject as Module;
            
            Rect rect = new Rect(0, 0, module.Width, module.Height);
            rect.Transform(GetTransformMatrix());
            return rect;
        }
    }
}
