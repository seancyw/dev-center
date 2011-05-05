using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;

namespace SpriteLightRock.Sprites
{
    enum ModuleType : byte
    {
        Image,
        Rectangle,
        Ellipse,
        Triangle
    }  
    class SpriteObject : DependencyObject
    {
        class LocalDef
        {
            public const double DEFAULT_X = 0;
            public const double DEFAULT_Y = 0;
            public const double DEFAULT_W = 10;
            public const double DEFAULT_H = 10;
        }

        #region Id
        public static readonly DependencyProperty IdProperty = DependencyProperty.Register(
            "Id",
            typeof(int),
            typeof(SpriteObject),
            new UIPropertyMetadata(null)
        );
        public int Id
        {
            get { return (int)GetValue(IdProperty); }
            set { SetValue(IdProperty, value); }
        }
        #endregion

        #region <bold>X</bold>
        public static readonly DependencyProperty RectProperty = DependencyProperty.Register(
            "Left",
            typeof(double),
            typeof(Rect),
            new UIPropertyMetadata(null)
        );
        public double X
        {
            get { return ((Rect)GetValue(RectProperty)).Left; }
            set { SetValue((Rect)GetValue(RectProperty)).Left, value); }
        }
        #endregion


        public static readonly DependencyProperty YProperty = DependencyProperty.Register(
            "Y",
            typeof(double),
            typeof(SpriteObject),
            new UIPropertyMetadata(null)
        );
        public double Y
        {
            get { return (double)GetValue(YProperty); }
            set { SetValue(YProperty, value); }
        }

        public static readonly DependencyProperty WidthProperty = DependencyProperty.Register(
            "Width",
            typeof(double),
            typeof(Module),
            new UIPropertyMetadata(null)
        );
        public double Width
        {
            get { return (double)GetValue(WidthProperty); }
            set
            {
                if (value > 0)
                {
                    SetValue(WidthProperty, value);
                }
            }
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
            set 
            {
                if (value > 0)
                {
                    SetValue(HeightProperty, value);
                }
            }
        }

        public virtual int PreFixId
        {
            get { return 0; }
        }

        public SpriteObject(int id, double x, double y,double width, double height)
        {
            Id          = id;
            X           = x;
            Y           = y;
            Width       = width;
            Height      = height;
        }
        public SpriteObject(int id, double x, double y)
            :this(id,x,y, LocalDef.DEFAULT_W,LocalDef.DEFAULT_H)
        {   
        }
        public SpriteObject(int id)
            :this(id,LocalDef.DEFAULT_X,LocalDef.DEFAULT_Y)
        {
        }
    }
}
