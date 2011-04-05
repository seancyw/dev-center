using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using System.Windows.Shapes;
using System.Windows;
using System.Windows.Input;
namespace SpriteLightRock.Sprites
{
    class Module : SpriteLowObject
    {
        private class LocalDef
        {
            public const int FRE_FIX = 0x0100;
        }        

        public static readonly DependencyProperty WidthProperty = DependencyProperty.Register(
            "Width",
            typeof(double),
            typeof(Module),
            new UIPropertyMetadata(null)
        );
        public double Width {
            get { return (double)GetValue(WidthProperty); }
            set { SetValue(WidthProperty, value); }
        }

        public static readonly DependencyProperty HeightProperty = DependencyProperty.Register(
            "Height",
            typeof(double),
            typeof(Module),
            new UIPropertyMetadata(null)
        );
        public double Height
        {
            get { return (double)GetValue(HeightProperty); }
            set { SetValue(HeightProperty, value); }
        }
        
        public override int PreFixId
        {
            get
            {
                return LocalDef.FRE_FIX;
            }
        }
        public Module(ModuleType type, int id, double x, double y, double w, double h)
            :base(id,x,y)
        {
            Width       = w;
            Height      = h;
        }
        public Module(int id, double x, double y, double w, double h)
            :this(ModuleType.Image,id,x,y,w,h)
        {
        }
        public bool IsInside(Point p)
        {
            return p.X >= X && p.Y >= Y && p.X <= (X + Width) && p.Y <= (Y + Height);
        }
        public Rect GetBoundRect()
        {
            return new Rect(X, Y, Width, Height);
        }
        protected void OnMouseMove(MouseEventArgs e)
        {
            e.MouseDevice.OverrideCursor = Cursors.Hand;
        }        
    }
}
