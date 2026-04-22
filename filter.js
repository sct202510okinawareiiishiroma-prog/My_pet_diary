/**
 * 絞り込みメニュー（アコーディオン）の開閉制御
 */
document.addEventListener('DOMContentLoaded', () => {
    const toggleBtn = document.getElementById('filterToggle');
    const options = document.getElementById('filterOptions');

    if (toggleBtn && options) {
        toggleBtn.addEventListener('click', () => {
            // 1. メニューの表示/非表示を切り替え
            // CSSで display: none にしているので、プログラムで display を操作します
            const isOpening = options.style.display !== 'flex';
            
            options.style.display = isOpening ? 'flex' : 'none';

            // 2. ボタンに 'open' クラスを付け外し（矢印の回転用）
            if (isOpening) {
                toggleBtn.classList.add('open');
            } else {
                toggleBtn.classList.remove('open');
            }
        });
    }
});