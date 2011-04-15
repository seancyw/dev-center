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
    }
}
