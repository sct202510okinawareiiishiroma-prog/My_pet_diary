/**
 * 新規追加用のモーダルを開く
 */
function openModal() {
    setupModal("項目の追加", "", "", false);
}

/**
 * 編集・削除用のモーダルを開く
 * @param {number} id - 項目のID
 * @param {string} name - 項目の現在の名称
 */
function openEditModal(id, name) {
    setupModal("項目の編集", id, name, true);
}

/**
 * モーダルの表示内容を切り替える共通処理
 */
function setupModal(title, id, name, isEdit) {
    const modal = document.getElementById("customItemModal");
    const form = document.getElementById("customItemForm");
    const titleElem = document.getElementById("modalTitle");
    const idInput = document.getElementById("itemId");
    const nameInput = document.getElementById("itemNameInput"); // idを合わせました
    const deleteBtn = document.getElementById("deleteButton");
    const calcTypeArea = document.getElementById("calcTypeArea"); // ラジオボタンエリア

    if (!modal) return;

    // 表示内容のセット
    titleElem.textContent = title;
    idInput.value = id;
    nameInput.value = name;
    
    // フォームのactionを初期状態（保存用）に戻す
//    form.action = "/save"; 

    // 編集モードなら削除ボタンを表示し、集計タイプ選択を隠す（仕様に合わせて調整可）
    if (isEdit) {
        deleteBtn.style.display = "inline-block";
        calcTypeArea.style.display = "none"; // 編集時はタイプ変更不可にする場合
    } else {
        deleteBtn.style.display = "none";
        calcTypeArea.style.display = "block";
    }

    modal.showModal();
    if (nameInput) nameInput.focus();
}

/**
 * 削除ボタンが押された時の処理
 */
function handleDelete() {
    if (confirm("この項目を削除してもよろしいですか？\n※これまでの記録データは保持されます。")) {
        const form = document.getElementById("customItemForm");
        // 送信先を一時的に削除用エンドポイントへ書き換えて送信
        form.action = "/custom-item/delete";
        form.submit();
    }
}

/**
 * モーダルを閉じる
 */
function closeModal() {
    const modal = document.getElementById("customItemModal");
    if (modal) {
        modal.close();
    }
}