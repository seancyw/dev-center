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
    class SpriteObj : DependencyObject
    {
        #region Id
        public static readonly DependencyProperty IdProperty = DependencyProperty.Register(
            "Id",
            typeof(int),
            typeof(SpriteObj),
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
            typeof(int),
            typeof(SpriteObj),
            new UIPropertyMetadata(null)
        );
        public int X
        {
            get { return (int)GetValue(XProperty); }
            set { SetValue(XProperty, value); }
        }
        #endregion


        public static readonly DependencyProperty YProperty = DependencyProperty.Register(
            "Y",
            typeof(int),
            typeof(SpriteObj),
            new UIPropertyMetadata(null)
        );
        public int Y
        {
            get { return (int)GetValue(YProperty); }
            set { SetValue(YProperty, value); }
        }
        public virtual int GetPreFixId(){return 0;}
    }
}
