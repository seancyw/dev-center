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

namespace SpriteLightRock
{
	/// <summary>
	/// Interaction logic for Window1.xaml
	/// </summary>
	public partial class Window1 : Window
	{
		public Window1()
		{
			InitializeComponent();
		}

        private void canvas1_MouseMove(object sender, MouseEventArgs e)
        {
            Ellipse myEllipse = new Ellipse();
            SolidColorBrush mySolidColorBrush = new SolidColorBrush();
            mySolidColorBrush.Color = Color.FromArgb(255, 255, 255, 0);
            myEllipse.Fill = mySolidColorBrush;
            myEllipse.StrokeThickness = 2;
            myEllipse.Stroke = Brushes.Black;

            myEllipse.Width = 200;
            myEllipse.Height = 100;

            myEllipse.RenderTransform = new RotateTransform(45);
            this.canvas1.Children.Add(myEllipse);

            
        }
	}
}