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
        private static Sprite           _currentSprite;
        private static ImageSource      _sprImage;
        private Point _currentPoint;
        public Sprite CurrentSprite
        {
            get
            {
                return _currentSprite;
            }
        }
        public void SetSprite(Sprite sprite)
        {
            _currentSprite = sprite;
        }
        
        public ImageSource SpriteImage { 
            get {
                if (_sprImage == null)
                {
                    if (CurrentSprite == null)
                        return null;
                    ImageSourceConverter imgConv = new ImageSourceConverter();
                    _sprImage = (ImageSource)imgConv.ConvertFromString(CurrentSprite.ImagePath);
                }
                return _sprImage;
            } 
        }
        public bool IsDrag
        {
            get;
            set;
        }
        public Point PointFressed
        {
            get;
            set;
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
            if (SpriteImage != null)
            {
                dc.DrawImage(SpriteImage, new Rect(0, 0, SpriteImage.Width, SpriteImage.Height));
            }
            Pen p = new Pen(Brushes.Green,1);
            if (IsDrag)
            {
                dc.DrawRectangle(Brushes.Transparent, p, new Rect(PointFressed, PointCurrent));
            }
            p = new Pen(Brushes.Red, 1);
            int moduleActiveId = CurrentSprite.GetActiveModuleId(PointCurrent);
            if (moduleActiveId != -1)
            {
                Module m = CurrentSprite.GetModule(moduleActiveId);
                dc.DrawRectangle(Brushes.Transparent, p, m.GetBoundRect());
            }
#if DEBUG
            Console.Write(String.Format("{0} {1}", PointFressed.X, PointFressed.Y));            
#endif
        }
        protected override void OnMouseDown(MouseButtonEventArgs e)
        {
            base.OnMouseDown(e);
            if (IsDrag == false)
            {
                IsDrag = true;
                PointFressed = e.GetPosition(this);                
            }
        }
        protected override void OnMouseMove(MouseEventArgs e)
        {
            base.OnMouseMove(e);
            PointCurrent = e.GetPosition(this);
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
