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
    class Module : SpriteObj
    {
        public static readonly DependencyProperty TypeProperty = DependencyProperty.Register(
            "Type",
            typeof(ModuleType),
            typeof(Module),
            new UIPropertyMetadata(null)
        );
        private ModuleType Type
        {
            get { return (ModuleType)GetValue(TypeProperty); }
            set { SetValue(TypeProperty, value); }
        }

        public static readonly DependencyProperty WidthProperty = DependencyProperty.Register(
            "Width",
            typeof(int),
            typeof(Module),
            new UIPropertyMetadata(null)
        );
        public int Width {
            get { return (int)GetValue(WidthProperty); }
            set { SetValue(WidthProperty, value); }
        }

        public static readonly DependencyProperty HeightProperty = DependencyProperty.Register(
            "Height",
            typeof(int),
            typeof(Module),
            new UIPropertyMetadata(null)
        );
        public int Height {
            get { return (int)GetValue(HeightProperty); }
            set { SetValue(HeightProperty, value); }
        }        
        
        public Module(ModuleType type, int id, int x, int y, int w, int h)            
        {
            Type = type;
            Id = id;
            X = x;
            Y = y;
            Width = w;
            Height = h;
        }
        public Module(int id, int x, int y, int w, int h)
            :this(ModuleType.Image,id,x,y,w,h)
        {
        }
        public bool IsInside(Point p)
        {
            if (Type == ModuleType.Image)
            {
                return p.X >= X && p.Y >= Y && p.X <= (X + Width) && p.Y <= (Y + Height);
            }
            return false;
        }
        public Rect GetBoundRect()
        {
            return new Rect(X, Y, Width, Height);
        }
        protected void OnMouseMove(MouseEventArgs e)
        {
            e.MouseDevice.OverrideCursor = Cursors.Hand;
        }
        public override int GetPreFixId()
        {
            return 0x1000;
        }
    }
}
