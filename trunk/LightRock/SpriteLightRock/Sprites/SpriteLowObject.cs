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
    class SpriteLowObject : DependencyObject
    {
        class LocalDef
        {
            public const double DEFAULT_X = 0;
            public const double DEFAULT_Y = 0;
        }
        #region Id
        public static readonly DependencyProperty IdProperty = DependencyProperty.Register(
            "Id",
            typeof(int),
            typeof(SpriteLowObject),
            new UIPropertyMetadata(null)
        );
        public int Id
        {
            get { return (int)GetValue(IdProperty); }
            set { SetValue(IdProperty, value); }
        }
        #endregion

        #region <bold>X</bold>
        public static readonly DependencyProperty XProperty = DependencyProperty.Register(
            "X",
            typeof(double),
            typeof(SpriteLowObject),
            new UIPropertyMetadata(null)
        );
        public double X
        {
            get { return (double)GetValue(XProperty); }
            set { SetValue(XProperty, value); }
        }
        #endregion


        public static readonly DependencyProperty YProperty = DependencyProperty.Register(
            "Y",
            typeof(double),
            typeof(SpriteLowObject),
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

        public virtual int PreFixId
        {
            get { return 0; }
        }

        public SpriteLowObject(int id, double x, double y)
        {
            Id = id;
            X = x;
            Y = y;
        }
        public SpriteLowObject(int id)
            :this(id,LocalDef.DEFAULT_X,LocalDef.DEFAULT_Y)
        {
        }
    }
}
