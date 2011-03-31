/*
 * Created by SharpDevelop.
 * User: phu.tranphong
 * Date: 3/30/2011
 * Time: 1:24 PM
 * 
 * To change this template use Tools | Options | Coding | Edit Standard Headers.
 */
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

namespace SpriteLightRock
{
    using Sprite;    
	/// <summary>
	/// Interaction logic for Window1.xaml
	/// </summary>
	public partial class SprEditorForm : Window
	{
        Point point1
        {
            get;
            set;
        }
        Point point2
        {
            get;
            set;
        }
        Boolean IsDrag
        {
            get;
            set;
        }
		public SprEditorForm()
		{
			InitializeComponent();
            ArrayList arrListModules = new ArrayList();
            for (int i = 0; i < 10; i++)
            {
                arrListModules.Add(new Module(1, 2, 3, 4));
            }
            this.lvModules.ItemsSource = arrListModules;            
		}

        void cnvWorkspace_MouseUp(object sender, MouseButtonEventArgs e)
        {
            IsDrag = false;
        }

        void cnvWorkspace_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (IsDrag == false)
            {
                IsDrag = true;
                Canvas canvas = sender as Canvas;
                point1 = e.GetPosition(canvas);
                Ellipse el = new Ellipse();
                el.Width = 2;
                el.Height = 2;
                el.Margin = new Thickness(e.GetPosition(canvas).X, e.GetPosition(canvas).Y, 0, 0);
                el.Fill = Brushes.YellowGreen;
                canvas.Children.Add(el);                
            }
        }
        

        void cnvWorkspace_DragEnter(object sender, DragEventArgs e)
        {
            Canvas canvas = sender as Canvas;
            Point point1 = e.GetPosition(canvas);
        }

        private void cnvWorkspace_MouseMove(object sender, MouseEventArgs e)
        {
            Canvas canvas = sender as Canvas;
            //canvas.Children.Clear();
            Ellipse el = new Ellipse();
            el.Width = 2;
            el.Height = 2;
            el.Margin = new Thickness(e.GetPosition(canvas).X, e.GetPosition(canvas).Y, 0, 0);
            el.Fill = Brushes.LimeGreen;
            //canvas.Children.Add(el);
            if (IsDrag)
            {
                Point point2 = e.GetPosition(canvas);
                Rectangle rect = new Rectangle();
                rect.Fill = Brushes.Red;                
                rect.Margin = new Thickness(point1.X, point1.Y, 0, 0);
                rect.Width = Math.Abs(point2.X- point1.X);
                rect.Height = Math.Abs(point2.Y- point1.Y);
                canvas.Children.Add(rect);
            }
        }
	}
}