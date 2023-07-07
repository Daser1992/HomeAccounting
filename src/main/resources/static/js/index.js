const dropMenuButton = document.querySelector('.chose_wallet_menu');
const listOfWallets = document.querySelector('.list_of_wallets');
const dropMenuButtonOfPurpose = document.querySelector('.using_purpose_menu');
const listOfPurposes = document.querySelector('.list-of-purposes');
let checkForListOfWallets = 0;
let checklistOfPurposes = 0;


function closeListOfWallets(event) {
    if (!event.target.closest('.list_of_wallets') && !event.target.closest('.chose_wallet_menu')) {
        listOfWallets.style.display = 'none';
        checkForListOfWallets = 0;
        document.removeEventListener('click', closeListOfWallets);
    }
}

dropMenuButton.addEventListener('click', function (event) {
    if (checkForListOfWallets === 0) {
        listOfWallets.style.display = 'block';
        checkForListOfWallets = 1;
        document.addEventListener('click', closeListOfWallets);
    } else {
        listOfWallets.style.display = 'none';
        checkForListOfWallets = 0;
        document.removeEventListener('click', closeListOfWallets);
    }
    event.stopPropagation();
});



function closeListOfPurposes(event) {
    if (!event.target.closest('.list-of-purposes') && !event.target.closest('.using_purpose_menu')) {
        listOfPurposes.style.display = 'none';
        checklistOfPurposes = 0;
        document.removeEventListener('click', closeListOfPurposes);
    }
}

dropMenuButtonOfPurpose .addEventListener('click', function (event) {
    if (checklistOfPurposes === 0) {
        listOfPurposes.style.display = 'block';
        checklistOfPurposes = 1;
        document.addEventListener('click', closeListOfPurposes);
    } else {
        listOfPurposes.style.display = 'none';
        checklistOfPurposes = 0;
        document.removeEventListener('click', closeListOfPurposes);
    }
    event.stopPropagation();
});





