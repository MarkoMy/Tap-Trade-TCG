import os

# Átnevezendő fájlokat tartalmazó mappa elérési útja
folder_path = 'S:/android/TapTradeTCG/app/src/main/assets/cards'  # cseréld ki a saját mappád elérési útjára

# Csak képfájlokat szűrünk ki (kiterjesztés alapján)
valid_extensions = ['.jpg', '.jpeg', '.png', '.bmp', '.gif']

# A fájlok betöltése és rendezése
files = [f for f in os.listdir(folder_path) if os.path.isfile(os.path.join(folder_path, f)) and os.path.splitext(f)[1].lower() in valid_extensions]
files.sort()

# Átnevezés
for i, filename in enumerate(files, start=1):
    ext = os.path.splitext(filename)[1].lower()
    new_name = f"{i:03d}{ext}"
    old_path = os.path.join(folder_path, filename)
    new_path = os.path.join(folder_path, new_name)
    os.rename(old_path, new_path)
    print(f"{filename} -> {new_name}")

print("Kész!")
