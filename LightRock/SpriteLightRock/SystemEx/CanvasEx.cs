#define CASE_1
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
    using Sprites;
    class CanvasEx : Canvas
    {
        static      private Sprite          _currentSprite;
        static      private MatrixTransform _transform;
        static private ScaleTransform       _scale = new ScaleTransform();
        static private TranslateTransform   _translate;
        static private     Point                   _currentPoint;
        static private Point _pressedPoint;
        private const double MAX_WIDTH = 10000;
        private const double MAX_HEIGHT = 10000;
        private const double MARGIN_LEFT = 100;
        private const double MARGIN_TOP = 100;

        public Point Offset
        {
            get
            {
                return new Point(Center.X - MARGIN_LEFT,Center.Y - MARGIN_TOP);
            }
        }

        public Point Center
        {
            get{
                return new Point(MAX_WIDTH / 2, MAX_HEIGHT / 2);
            }
        }

        public Sprite CurrentSprite
        {
            get
            {
                return _currentSprite;
            }
            set
            {
                _currentSprite = value;
            }
        }        
        
        public CanvasEx()
        	:base()
        {
            ClipToBounds = true;
            _transform = (MatrixTransform)MatrixTransform.Identity;
            _translate = new TranslateTransform(-Offset.X, -Offset.Y);
        }
        public bool IsDrag
        {
            get;
            set;
        }
        public Point PointFressed
        {
            get{return _pressedPoint;}
            set{_pressedPoint = value;}
        }
        public Point PointCurrent
        {
            get { return _currentPoint; }
            set{
                if (_currentPoint != value)
                {
                    _currentPoint = value;
                    this.InvalidateVisual();
                }
            }
        }
        protected override void OnRender(DrawingContext dc)
        {
            base.OnRender(dc);
            if (CurrentSprite == null)
                return;
            //_transform.Value.Rotate(45);
            dc.PushTransform(_scale);            
            dc.PushTransform(_translate);
            
            dc.DrawLine(new Pen(Brushes.Red, 1),new Point(Center.X,0),new Point(Center.X,MAX_HEIGHT));
            dc.DrawLine(new Pen(Brushes.Green, 1), new Point(0, Center.Y), new Point(MAX_WIDTH, Center.Y));
            dc.DrawImage(CurrentSprite.Bitmap, new Rect(Center.X,Center.Y,CurrentSprite.Bitmap.Width,CurrentSprite.Bitmap.Height));

            dc.DrawRectangle(Brushes.Red, new Pen(Brushes.Violet, 2), new Rect(PointFressed, new Size(10, 10)));
        }
        protected override void OnMouseDown(MouseButtonEventArgs e)
        {
            base.OnMouseDown(e);
            if (IsDrag == false)
            {
                IsDrag = true;
                PointFressed = e.GetPosition(this);
#if !CASE_1
                
#else
                PointFressed = new Point(PointFressed.X + Offset.X, PointFressed.Y + Offset.Y);                

#endif
            }
        }
        protected override void OnMouseMove(MouseEventArgs e)
        {
            base.OnMouseMove(e);
            PointCurrent = e.GetPosition(this);
        }
        protected override void OnMouseWheel(MouseWheelEventArgs e)
        {
            if (e.Delta > 0)
            {
                _scale.ScaleX += 0.1;
                _scale.ScaleY += 0.1;
            }
            else
            {
                _scale.ScaleX -= 0.1;
                _scale.ScaleY -= 0.1;
            }
            base.OnMouseWheel(e);
        }
        //Hien tai cai canvas khong the nhan dc su kien bam phim
        protected override void OnKeyDown(KeyEventArgs e)
        {
            if (e.Key == Key.NumPad1)
            {
                _scale.ScaleX = 1;
                _scale.ScaleY = 1;
            }
            base.OnKeyDown(e);
        }
        protected override void OnMouseUp(MouseButtonEventArgs e)
        {
            base.OnMouseUp(e);
            if (IsDrag == true)
            {
                Rect rect = new Rect(PointFressed, PointCurrent);
                CurrentSprite.Modules.Add(new Module(CurrentSprite.GetAutoGenerateModuleId(), (int)rect.Left, (int)rect.Top, (int)rect.Width, (int)rect.Height));
                IsDrag = false;
            }
        }
    }
}
