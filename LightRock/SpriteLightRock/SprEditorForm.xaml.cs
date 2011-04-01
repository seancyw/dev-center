using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace SpriteLightRock
{
    using Sprites;
    /// <summary>
    /// Interaction logic for SprEditorForm.xaml
    /// </summary>
    public partial class SprEditorForm : UserControl
    {
        public SprEditorForm()
        {
            InitializeComponent();
            Sprite sprite = new Sprite();
            for(int i = 0; i < 10; i++)
            {
                sprite.Modules.Add(new Module(i+1,1,2,3,4));
            }
            lvViewModules.ItemsSource = sprite.Modules;
        }
    }
}
