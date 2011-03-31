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
        public bool IsDrag
        {
            get;
            set;
        }
        public Point Point1
        {
            get;
            set;
        }
        public Point Point2
        {
            get;
            set;
        }
        public ImageSource SpriteImage
        {
            get;
            set;
        }
        protected override void OnRender(DrawingContext dc)
        {
            base.OnRender(dc);
            if (SpriteImage != null)
            {
                dc.DrawImage(SpriteImage, new Rect(0, 0, SpriteImage.Width, SpriteImage.Height));
            }
            Pen p = new Pen(Brushes.Green,1);
            dc.DrawRectangle(Brushes.Red, p, new Rect(Point1, Point2));
            Console.Write(String.Format("{0} {1}", Point1.X, Point1.Y));
        }
        protected override void OnMouseDown(MouseButtonEventArgs e)
        {
            base.OnMouseDown(e);
            if (IsDrag == false)
            {
                IsDrag = true;
                Point1 = e.GetPosition(this);                
            }
        }
        protected override void OnMouseMove(MouseEventArgs e)
        {
            base.OnMouseMove(e);
            if (IsDrag)
            {
                Point2 = e.GetPosition(this);                
            }
            UpdateLayout();            
        }
        protected override void OnMouseUp(MouseButtonEventArgs e)
        {
            base.OnMouseUp(e);
            IsDrag = false;
        }
    }
}
