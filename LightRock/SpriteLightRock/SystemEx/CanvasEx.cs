using System;
using System.Collections.Generic;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Shapes;
using System.Collections;
namespace SpriteLightRock.SystemEx
{
    class CanvasEx : Canvas
    {
        public Image BgImage
        {
            get;
            set;
        }
        protected override void OnRender(DrawingContext dc)
        {
            base.OnRender(dc);
        }
    }
}
