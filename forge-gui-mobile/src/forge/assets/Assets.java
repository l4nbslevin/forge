package forge.assets;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.tommyettinger.textra.Font;
import forge.Forge;
import forge.gui.GuiBase;
import forge.localinstance.properties.ForgeConstants;
import forge.localinstance.skin.FSkinProp;

import java.util.HashMap;
import java.util.Map;

public class Assets implements Disposable {
    private MemoryTrackingAssetManager manager;
    private HashMap<Integer, FSkinFont> fonts;
    private HashMap<String, FImageComplex> cardArtCache;
    private HashMap<String, FImage> avatarImages;
    private HashMap<String, FSkinImage> manaImages;
    private HashMap<String, FSkinImage> symbolLookup;
    private HashMap<FSkinProp, FSkinImage> images;
    private HashMap<Integer, TextureRegion> avatars;
    private HashMap<Integer, TextureRegion> sleeves;
    private HashMap<Integer, TextureRegion> cracks;
    private HashMap<Integer, TextureRegion> borders;
    private HashMap<Integer, TextureRegion> deckbox;
    private HashMap<Integer, TextureRegion> cursor;
    private ObjectMap<Integer, BitmapFont> counterFonts;
    private ObjectMap<String, Texture> generatedCards;
    private ObjectMap<String, Texture> fallback_skins;
    private ObjectMap<String, Texture> tmxMap;
    private Texture defaultImage, dummy, deckImage, sideboardImage, binderImage, sellIconImage;
    private Texture hdLogoTexture, advLogoTexture, overlay_alpha, splatter;
    private TextureParameter textureParameter;
    private ObjectMap<String, Font> textrafonts;
    private int cGen = 0, cGenVal = 0, cFB = 0, cFBVal = 0, cTM = 0, cTMVal = 0, cSF = 0, cSFVal = 0, cCF = 0, cCFVal = 0, aDF = 0, cDFVal = 0;

    public Assets() {
        String titleFilename = Forge.isLandscapeMode() ? "title_bg_lq.png" : "title_bg_lq_portrait.png";
        try {
            //init titleLQ
            if (GuiBase.isAndroid())
                getTexture(Gdx.files.internal("fallback_skin").child(titleFilename));
            else
                getTexture(Gdx.files.classpath("fallback_skin").child(titleFilename));
            //init transition
            if (GuiBase.isAndroid())
                getTexture(Gdx.files.internal("fallback_skin").child("transition.png"));
            else
                getTexture(Gdx.files.classpath("fallback_skin").child("transition.png"));
        } catch (Exception e) {
            fallback_skins().clear();
            fallback_skins().put("title", getDummy());
            fallback_skins().put("transition", getDummy());
        }
    }

    @Override
    public void dispose() {
        try {
            if (counterFonts != null)
                for (BitmapFont bitmapFont : counterFonts.values())
                    bitmapFont.dispose();
            if (generatedCards != null)
                for (Texture texture : generatedCards.values())
                    texture.dispose();
            if (fallback_skins != null)
                for (Texture texture : fallback_skins.values())
                    texture.dispose();
            if (tmxMap != null)
                for (Texture texture : tmxMap.values())
                    texture.dispose();
            if (defaultImage != null)
                defaultImage.dispose();
            if (dummy != null)
                dummy.dispose();
            if (textrafonts != null) {
                for (Font f : textrafonts.values())
                    f.dispose();
            }
            cardArtCache.clear();
            avatarImages.clear();
            manaImages.clear();
            symbolLookup.clear();
            images.clear();
            avatars.clear();
            sleeves.clear();
            cracks.clear();
            borders.clear();
            deckbox.clear();
            cursor.clear();
            fonts.clear();
            counterFonts.clear();
            generatedCards.clear();
            fallback_skins.clear();
            tmxMap.clear();
            manager.dispose();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public MemoryTrackingAssetManager manager() {
        if (manager == null)
            manager = new MemoryTrackingAssetManager(new AbsoluteFileHandleResolver());
        return manager;
    }

    public HashMap<Integer, FSkinFont> fonts() {
        if (fonts == null)
            fonts = new HashMap<>();
        return fonts;
    }

    public HashMap<String, FImageComplex> cardArtCache() {
        if (cardArtCache == null)
            cardArtCache = new HashMap<>();
        return cardArtCache;
    }

    public HashMap<String, FImage> avatarImages() {
        if (avatarImages == null)
            avatarImages = new HashMap<>();
        return avatarImages;
    }

    public HashMap<String, FSkinImage> manaImages() {
        if (manaImages == null)
            manaImages = new HashMap<>();
        return manaImages;
    }

    public HashMap<String, FSkinImage> symbolLookup() {
        if (symbolLookup == null)
            symbolLookup = new HashMap<>();
        return symbolLookup;
    }

    public HashMap<FSkinProp, FSkinImage> images() {
        if (images == null)
            images = new HashMap<>();
        return images;
    }

    public HashMap<Integer, TextureRegion> avatars() {
        if (avatars == null)
            avatars = new HashMap<>();
        return avatars;
    }

    public HashMap<Integer, TextureRegion> sleeves() {
        if (sleeves == null)
            sleeves = new HashMap<>();
        return sleeves;
    }

    public HashMap<Integer, TextureRegion> cracks() {
        if (cracks == null)
            cracks = new HashMap<>();
        return cracks;
    }

    public HashMap<Integer, TextureRegion> borders() {
        if (borders == null)
            borders = new HashMap<>();
        return borders;
    }

    public HashMap<Integer, TextureRegion> deckbox() {
        if (deckbox == null)
            deckbox = new HashMap<>();
        return deckbox;
    }

    public HashMap<Integer, TextureRegion> cursor() {
        if (cursor == null)
            cursor = new HashMap<>();
        return cursor;
    }

    public ObjectMap<Integer, BitmapFont> counterFonts() {
        if (counterFonts == null)
            counterFonts = new ObjectMap<>();
        return counterFonts;
    }

    public ObjectMap<String, Texture> generatedCards() {
        if (generatedCards == null)
            generatedCards = new ObjectMap<>();
        return generatedCards;
    }

    public ObjectMap<String, Texture> fallback_skins() {
        if (fallback_skins == null)
            fallback_skins = new ObjectMap<String, Texture>() {
                @Override
                public Texture put(String key, Texture value) {
                    Texture old = remove(key);
                    if (old != null)
                        old.dispose();
                    return super.put(key, value);
                }
            };
        return fallback_skins;
    }

    public ObjectMap<String, Texture> tmxMap() {
        if (tmxMap == null)
            tmxMap = new ObjectMap<>();
        return tmxMap;
    }

    public TextureParameter getTextureFilter() {
        if (textureParameter == null)
            textureParameter = new TextureParameter();
        if (Forge.isTextureFilteringEnabled()) {
            textureParameter.genMipMaps = true;
            textureParameter.minFilter = Texture.TextureFilter.MipMapLinearLinear;
            textureParameter.magFilter = Texture.TextureFilter.Linear;
        } else {
            textureParameter.genMipMaps = false;
            textureParameter.minFilter = Texture.TextureFilter.Nearest;
            textureParameter.magFilter = Texture.TextureFilter.Nearest;
        }
        return textureParameter;
    }

    public Texture getTexture(FileHandle file) {
        return getTexture(file, true);
    }
    public Texture getTexture(FileHandle file, boolean required) {
        if (file == null || !file.exists()) {
            if (!required)
                return null;
            System.err.println("Failed to load: " + file +"!. Creating dummy texture.");
            return getDummy();
        }
        //internal path can be inside apk or jar..
        if (!FileType.Absolute.equals(file.type()) || file.path().contains("fallback_skin")) {
            Texture f = fallback_skins().get(file.path());
            if (f == null) {
                f = new Texture(file);
                fallback_skins().put(file.path(), f);
            }
            return f;
        }
        Texture t = manager().get(file.path(), Texture.class, false);
        if (t == null) {
            manager().load(file.path(), Texture.class, getTextureFilter());
            manager().finishLoadingAsset(file.path());
            t = manager().get(file.path(), Texture.class);
        }
        return t;
    }

    public Texture getDefaultImage() {
        if (defaultImage == null) {
            FileHandle blankImage = Gdx.files.absolute(ForgeConstants.NO_CARD_FILE);
            if (blankImage.exists()) {
                defaultImage = manager().get(blankImage.path(), Texture.class, false);
                if (defaultImage != null)
                    return defaultImage;
                //if not loaded yet, load to assetmanager
                manager().load(blankImage.path(), Texture.class, getTextureFilter());
                manager().finishLoadingAsset(blankImage.path());
                defaultImage = manager().get(blankImage.path());
            } else {
                defaultImage = getDummy();
            }
        }
        return defaultImage;
    }

    public Texture getDeckImage(FileHandle file) {
        if (file == null || !file.exists())
            return null;
        deckImage = manager().get(file.path(), Texture.class, false);
        if (deckImage == null) {
            manager().load(file.path(), Texture.class, getTextureFilter());
            manager().finishLoadingAsset(file.path());
            deckImage = manager().get(file.path(), Texture.class);
        }
        return deckImage;
    }

    public Texture getSideboardImage(FileHandle file) {
        if (file == null || !file.exists())
            return null;
        sideboardImage = manager().get(file.path(), Texture.class, false);
        if (sideboardImage == null) {
            manager().load(file.path(), Texture.class, getTextureFilter());
            manager().finishLoadingAsset(file.path());
            sideboardImage = manager().get(file.path(), Texture.class);
        }
        return sideboardImage;
    }

    public Texture getBinderImage(FileHandle file) {
        if (file == null || !file.exists())
            return null;
        binderImage = manager().get(file.path(), Texture.class, false);
        if (binderImage == null) {
            manager().load(file.path(), Texture.class, getTextureFilter());
            manager().finishLoadingAsset(file.path());
            binderImage = manager().get(file.path(), Texture.class);
        }
        return binderImage;
    }

    public Texture getSellIconImage(FileHandle file) {
        if (file == null || !file.exists())
            return null;
        sellIconImage = manager().get(file.path(), Texture.class, false);
        if (sellIconImage == null) {
            manager().load(file.path(), Texture.class, getTextureFilter());
            manager().finishLoadingAsset(file.path());
            sellIconImage = manager().get(file.path(), Texture.class);
        }
        return sellIconImage;
    }

    public Texture getHdLogoTexture(FileHandle file) {
        if (file == null || !file.exists())
            return null;
        if (hdLogoTexture == null) {
            manager().load(file.path(), Texture.class, getTextureFilter());
            manager().finishLoadingAsset(file.path());
            hdLogoTexture = manager().get(file.path(), Texture.class);
        }
        return hdLogoTexture;
    }

    public Texture getAdvLogoTexture(FileHandle file) {
        if (file == null || !file.exists())
            return null;
        if (advLogoTexture == null) {
            manager().load(file.path(), Texture.class, getTextureFilter());
            manager().finishLoadingAsset(file.path());
            advLogoTexture = manager().get(file.path(), Texture.class);
        }
        return advLogoTexture;
    }

    public Texture getSplatter() {
        return splatter;
    }

    public Texture getOverlay_alpha() {
        return overlay_alpha;
    }

    public void loadTexture(FileHandle file) {
        loadTexture(file, getTextureFilter());
    }
    public void loadTexture(FileHandle file, TextureParameter parameter) {
        try {
            if (file == null || !file.exists())
                return;
            if (!FileType.Absolute.equals(file.type()))
                return;
            manager().load(file.path(), Texture.class, parameter);
            manager().finishLoadingAsset(file.path());
            if (file.path().contains("overlay_alpha.png"))
                overlay_alpha = manager().get(file.path(), Texture.class, false);
            if (file.path().contains("splatter.png"))
                splatter = manager().get(file.path(), Texture.class, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Texture getDummy() {
        if (dummy == null) {
            Pixmap P = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            P.setColor(0f, 0f, 0f, 1f);
            P.drawPixel(0, 0);
            dummy = new Texture(P);
            P.dispose();
        }
        return dummy;
    }

    public Font getTextraFont(BitmapFont bitmapFont, TextureAtlas item_atlas, TextureAtlas pixelmana_atlas) {
        if (textrafonts == null)
            textrafonts = new ObjectMap<>();
        if (!textrafonts.containsKey("textrafont")) {
            Font font = new Font(bitmapFont, 0f, 2f, 0f, 0f);
            font.addAtlas(item_atlas, 0f, 0f, 0f);
            font.addAtlas(pixelmana_atlas, 0f, -12f, 0f);
            font.integerPosition = false;
            textrafonts.put("textrafont", font);
        }
        return textrafonts.get("textrafont");
    }

    public Font getKeysFont(BitmapFont bitmapFont, TextureAtlas keys_atlas) {
        if (textrafonts == null)
            textrafonts = new ObjectMap<>();
        if (!textrafonts.containsKey("keysfont")) {
            Font font = new Font(bitmapFont);
            font.addAtlas(keys_atlas);
            font.integerPosition = false;
            textrafonts.put("keysfont", font);
        }
        return textrafonts.get("keysfont");
    }

    public Font getTextraFont(String name, BitmapFont bitmapFont, TextureAtlas items_atlas) {
        if (textrafonts == null)
            textrafonts = new ObjectMap<>();
        if (!textrafonts.containsKey(name)) {
            Font font = new Font(bitmapFont);
            font.addAtlas(items_atlas);
            font.integerPosition = false;
            textrafonts.put(name, font);
        }
        return textrafonts.get(name);
    }

    public class MemoryTrackingAssetManager extends AssetManager {
        private int currentMemory;
        private Map<String, Integer> memoryPerFile;

        public MemoryTrackingAssetManager(FileHandleResolver resolver) {
            super(resolver);

            currentMemory = 0;
            memoryPerFile = new HashMap<String, Integer>();
        }

        @SuppressWarnings("unchecked")
        private int calculateTextureSize(AssetManager assetManager, String fileName, Class type) {
            if (!Forge.showFPS)
                return 0;
            Texture texture = (Texture) assetManager.get(fileName, type);
            TextureData textureData = texture.getTextureData();
            int textureSize = textureData.getWidth() * textureData.getHeight();
            if (Forge.isTextureFilteringEnabled())
                textureSize = textureSize + (textureSize / 3);
            switch (textureData.getFormat()) {
                case RGB565:
                    textureSize *= 2;
                    break;
                case RGB888:
                    textureSize *= 3;
                    break;
                case RGBA4444:
                    textureSize *= 2;
                    break;
                case RGBA8888:
                    textureSize *= 4;
                    break;
            }
            memoryPerFile.put(fileName, textureSize);

            return memoryPerFile.values().stream().mapToInt(Integer::intValue).sum() + calcFonts() + calcCounterFonts()
                    + calculateObjectMaps(generatedCards()) + calculateObjectMaps(fallback_skins()) + calculateObjectMaps(tmxMap());
        }

        @SuppressWarnings("unchecked")
        private int calculateObjectMaps(ObjectMap<?, Texture> objectMap) {
            if (!Forge.showFPS)
                return 0;
            if (objectMap == null || objectMap.isEmpty())
                return 0;
            if (objectMap == generatedCards) {
                if (cGen == objectMap.size)
                    return cGenVal;
                else
                    cGen = objectMap.size;
            }
            if (objectMap == tmxMap) {
                if (cTM == objectMap.size)
                    return cTMVal;
                else
                    cTM = objectMap.size;
            }
            if (objectMap == fallback_skins) {
                if (cFB == objectMap.size)
                    return cFBVal;
                else
                    cFB = objectMap.size;
            }
            int sum = 0;
            for (Texture texture : objectMap.values()) {
                TextureData textureData = texture.getTextureData();
                int textureSize = textureData.getWidth() * textureData.getHeight();
                if (Forge.isTextureFilteringEnabled())
                    textureSize = textureSize + (textureSize / 3);
                switch (textureData.getFormat()) {
                    case RGB565:
                        textureSize *= 2;
                        break;
                    case RGB888:
                        textureSize *= 3;
                        break;
                    case RGBA4444:
                        textureSize *= 2;
                        break;
                    case RGBA8888:
                        textureSize *= 4;
                        break;
                }
                sum += textureSize;
            }
            if (objectMap == generatedCards)
                cGenVal = sum;
            if (objectMap == tmxMap)
                cTMVal = sum;
            if (objectMap == fallback_skins)
                cFBVal = sum;
            return sum;
        }

        private int calcFonts() {
            if (!Forge.showFPS)
                return 0;
            if (fonts == null || fonts.isEmpty())
                return 0;
            if (cSF == fonts.size())
                return cSFVal;
            cSF = fonts.size();
            int val = 0;
            for (FSkinFont sf : fonts.values()) {
                val += calcBitmapFont(sf.font);
            }
            cSFVal = val;
            return cSFVal;
        }

        private int calcCounterFonts() {
            if (!Forge.showFPS)
                return 0;
            if (counterFonts == null || counterFonts.isEmpty())
                return 0;
            if (cCF == counterFonts.size)
                return cCFVal;
            int val = 0;
            for (BitmapFont cf : counterFonts.values()) {
                val += calcBitmapFont(cf);
            }
            cCFVal = val;
            return cCFVal;
        }

        private int calcBitmapFont(BitmapFont bitmapFont) {
            if (bitmapFont == null)
                return 0;
            int val = 0;
            for (TextureRegion tr : bitmapFont.getRegions()) {
                Texture t = tr.getTexture();
                val += (t.getWidth() * t.getHeight()) * 4;
            }
            return val;
        }

        @SuppressWarnings("unchecked")
        @Override
        public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
            if (type.equals(Texture.class)) {
                if (parameter == null) {
                    parameter = (AssetLoaderParameters<T>) getTextureFilter();
                }

                final AssetLoaderParameters.LoadedCallback prevCallback = parameter.loadedCallback;
                parameter.loadedCallback = (assetManager, fileName1, type1) -> {
                    if (prevCallback != null) {
                        prevCallback.finishedLoading(assetManager, fileName1, type1);
                    }

                    currentMemory = calculateTextureSize(assetManager, fileName1, type1);
                };

            }

            super.load(fileName, type, parameter);
        }

        @Override
        public synchronized void unload(String fileName) {
            super.unload(fileName);
            if (memoryPerFile.containsKey(fileName)) {
                memoryPerFile.remove(fileName);
            }
            cardArtCache().clear();
        }

        public float getMemoryInMegabytes() {
            return (float) currentMemory / 1024f / 1024f;
        }
    }
}
