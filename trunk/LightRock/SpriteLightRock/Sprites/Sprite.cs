using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;
using System.Windows;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.Windows.Input;

namespace SpriteLightRock.Sprites
{
    enum ViewState{
        Module,
        FModule,
        Frame,
        AFrame,
        VFrame
    }
    class Sprite
    {
        private ObservableCollection<Module>        _listModules;
        private ObservableCollection<Frame>         _listFrames;
        private ObservableCollection<AFrame>        _listAFrames;
        private string          _imageFile;        
        private ViewState       _viewState;
        private BitmapImage		_bitmap;
        //@Reference: http://www.switchonthecode.com/tutorials/wpf-tutorial-using-the-listview-part-1
        //&http://msdn.microsoft.com/en-us/library/ms752347%28v=VS.85%29.aspx
        public ObservableCollection<Module> Modules
        {
            get
            {
                return _listModules;
            }
            private set
            {
                _listModules = value;
            }
        }
        public BitmapImage Bitmap
        {
        	get{return _bitmap;}
        }
        public ViewState View
        {
            get
            {
                return _viewState;
            }
            set
            {
                _viewState = value;
            }
        }
        
        public string ImageFile
        {
            get { return _imageFile;}
            set{ _imageFile = value;}
        }
        
        public Sprite()
        {
            Modules = new ObservableCollection<Module>();
            View        = ViewState.Module;
        }
        
        public bool LoadImage()
        {
        	_bitmap = new BitmapImage();
        	_bitmap.BeginInit();
        	_bitmap.UriSource = new Uri(ImageFile,UriKind.Absolute);
        	_bitmap.EndInit();
        	return true;
        }
        
        public Module GetModule(int moduleId)
        {
            for (int i = 0; i < Modules.Count; i++)
            {
                if (Modules[i].Id == moduleId)
                    return Modules[i];
            }
            return null;
        }
        public int GetAutoGenerateModuleId()
        {
            //Cho nay code khong dung, nhung tam vay
            if (Modules.Count == 0)
                return 1;
            return Modules[Modules.Count - 1].Id + 1;
        }
        public int GetActiveModuleId(Point p)
        {
            for (int i = 0; i < Modules.Count; i++)
            {
                if(Modules[i].IsInside(p))
                    return Modules[i].Id;
            }
            return -1;
        }
    }
}
