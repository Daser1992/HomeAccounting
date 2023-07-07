// document.addEventListener("DOMContentLoaded", function (){
    if (sessionStorage.length > 0){
        const wallets = document.querySelectorAll('.wallet-forEach');
        wallets.forEach(function (wallet){
            let sum = wallet.querySelector('.wallet-sum').textContent;
            if (wallet.querySelector('.wallet-name').textContent === sessionStorage.getItem("walletName") &&
            sum !== sessionStorage.getItem("walletSum")){

                const result  = parseFloat(sum);
                sessionStorage.setItem("walletSum", result.toFixed(2));
            }
        })
        $('.current-wallet-name').text(sessionStorage.getItem("walletName"));
        $('.current-wallet-sum').text(sessionStorage.getItem("walletSum"));
        $('.current-wallet-currency').text(sessionStorage.getItem("walletCurrency"));
    }
// })

const Toast = Swal.mixin({
    toast: true,
    position: 'center',
    showConfirmButton: false,
    timer: 1600
});

const changeCurrentSum = (sum, sign) =>{
    let currentSum = $('.current-wallet-sum').text();
    currentSum = parseFloat(currentSum);
    if (sign === '+'){
        currentSum -= sum;
    }else {
        currentSum += sum;
    }
    $('.current-wallet-sum').text(currentSum.toFixed(2));
    sessionStorage.setItem("walletSum", currentSum.toFixed(2).toString());
    const wallets = document.querySelectorAll('.wallet-forEach');
    wallets.forEach(function (wallet){
        if (wallet.querySelector('.wallet-name').textContent === $('.current-wallet-name').text()){
            wallet.querySelector('.wallet-sum').innerHTML = currentSum.toFixed(2);
        }
    });
}


// Транзакції за місяць
const table = $('.table-striped tbody');
const walletTransactionsByMonth = (numberOfMonth, walletName) => {

    $.getJSON('/private_office/wallet_transactions_by_month', {
        month: numberOfMonth,
        walletName: walletName
    }, function (response) {
        table.text("");
        if (response.length === 0) {
            $('.table-striped tbody').append($("<tr>").append($('<th colspan="6">').text("За вибраним вами місяцем записів не знайдено")));
            return;
        }
        let tableHTML = response.map((transaction) => {
            let resultByMonth = $('<tr>');
            let tdWalletName = $('<th>').text(transaction.walletName);
            let tdDate = $('<td>').text(transaction.date);
            let tdPurpose = $('<td>').text(transaction.purpose);
            let tdSign;
            if (transaction.sign === '+') {
                tdSign = $('<td>').addClass('arrow_left').html(' &lArr; ');
            } else {
                tdSign = $('<td>').addClass('arrow_right').html(' &rArr; ');
            }

            let tdSuma = $('<td>').text(transaction.suma.toFixed(2) + transaction.walletCurrency);
            let imgForTd = $('<img src="./img/Icon/1.png">').addClass('img-x');
            let tdRemove = $('<td class="remove-transaction">').append(imgForTd);
            resultByMonth.append(tdWalletName, tdDate, tdPurpose, tdSign, tdSuma, tdRemove);

            imgForTd.on('click', () => {
                Swal.fire({
                    title: 'Ви впевнені, що хочете видалити цей запис?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Так, Видалити',
                    cancelButtonText: 'Відмінити'
                }).then((result) => {
                    if (result.isConfirmed) {
                        console.log(transaction.id);
                        const transactionObject = {
                            sum: transaction.suma,
                            sign: transaction.sign
                        }
                        fetch('/remove_transaction?id=' + transaction.id, {method: 'DELETE'}).then(res => {
                            if (res.ok) {
                                changeCurrentSum(transactionObject.sum, transactionObject.sign);
                                resultByMonth.css('display', 'none');
                                Toast.fire({
                                    icon: 'success',
                                    title: 'Запис видалено успішно.'
                                });
                                console.log('Transaction deleted successfully.');

                            }else {
                                throw new Error('Network response was not ok');
                            }

                        });
                    }
                })

            });

            return resultByMonth;
        });
        table.append(tableHTML);

    });
}


// Показ всих транзакцій
let varPage;
const getAllTransactionByWallet = (page) => {

    fetch('/get_all_transaction_by_wallet?walletName=' + $('.current-wallet-name').text() + '&page=' + page + '&howMuch=' + 30).then(res => res.json())
        .then(walletTransactions => {
            const table =  $('.table-striped tbody');
            if (walletTransactions.length === 0) {
               table.text('');
                table.append($("<tr>").append($('<th colspan="6">').text('Записів не знайдено')));
                return;
            }
            console.log(walletTransactions);
            if (page === 0) {
                table.text("");
            }

            let tableHTML = walletTransactions.map((transaction) => {
                let resultForAll = $('<tr>');
                let tdWalletName = $('<th>').text(transaction.walletName);
                let tdDate = $('<td>').text(transaction.date);
                let tdPurpose = $('<td>').text(transaction.purpose);
                let tdSign;
                if (transaction.sign === '+') {
                    tdSign = $('<td>').addClass('arrow_left').html(' &lArr; ');
                } else {
                    tdSign = $('<td>').addClass('arrow_right').html(' &rArr; ');
                }
                let tdSuma = $('<td>').text(transaction.suma.toFixed(2) + transaction.walletCurrency);
                let imgForTd = $('<img src="./img/Icon/1.png">').addClass('img-x');
                let tdRemove = $('<td class="remove-transaction">').append(imgForTd);

                resultForAll.append(tdWalletName, tdDate, tdPurpose, tdSign, tdSuma, tdRemove);

                imgForTd.on('click', () => {
                    Swal.fire({
                        title: 'Ви впевнені, що хочете видалити цей запис?',
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#3085d6',
                        cancelButtonColor: '#d33',
                        confirmButtonText: 'Так, Видалити',
                        cancelButtonText: 'Відмінити'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            console.log(transaction.id);
                            const transactionObject = {
                                sum: transaction.suma,
                                sign: transaction.sign
                            }
                            fetch('/remove_transaction?id=' + transaction.id, {method: 'DELETE'}).then(res => {
                                if (res.ok) {
                                    changeCurrentSum(transactionObject.sum, transactionObject.sign);
                                    resultForAll.css('display', 'none');
                                    Toast.fire({
                                        icon: 'success',
                                        title: 'Запис видалено успішно.'
                                    });
                                    console.log('Transaction deleted successfully.');

                                } else {
                                    throw new Error('Network response was not ok');
                                }

                            });

                        }
                    })

                });

                return resultForAll;
            });
            if (walletTransactions.length === 20) {
                $('.show-more').removeClass("show-more-remove");
            } else $('.show-more').addClass("show-more-remove");
            console.log(tableHTML);
            table.append(tableHTML);
            varPage++;
        });
}

// Вибір місяця для показу записів

let choseMonths = $('.chose-month');

for (let i = 0; i < choseMonths.length; i++) {
    choseMonths[i].addEventListener('click', function () {
        walletTransactionsByMonth(i + 1, $('.current-wallet-name').text());
    });
}

// Для кожно гаманця додається слухач події

let walletNames = $('.wallet-forEach');

for (let i = 0; i < walletNames.length; i++) {

    walletNames[i].addEventListener('click', () => {
        const currentWalletName = $('.current-wallet-name');
        const currentWalletSum = $('.current-wallet-sum');
        const currentWalletCurrency = $('.current-wallet-currency');

        let fixedSum = walletNames[i].querySelector('.wallet-sum').textContent;
        fixedSum = parseFloat(fixedSum);

        currentWalletName.text(walletNames[i].querySelector(".wallet-name").textContent);
        currentWalletSum.text(fixedSum.toFixed(2));
        currentWalletCurrency.text(walletNames[i].querySelector(".wallet-currency").textContent);
        sessionStorage.setItem("walletName", currentWalletName.text());
        sessionStorage.setItem("walletSum", currentWalletSum.text());
        sessionStorage.setItem("walletCurrency", currentWalletCurrency.text());
        let test = currentWalletSum.text();
        let testSum = parseFloat(test.replace('грн', ''));
        console.log(currentWalletName.text())
        console.log(testSum);

    });
}

// Видалення гаманця

const walletNameForDelete = $('#name-of-wallet-for-delete');

$('.btn-for-delete-wallet').on('click', (event) => {
    event.preventDefault();
    if (walletNameForDelete.val().trim() === '') {
        return;
    }
    Swal.fire({
        title: 'Ви впевнені?',
        text: "Ви впевнені, що хочете видалити " + walletNameForDelete.val(),
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Так, Видалити',
        cancelButtonText: 'Відмінити'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch('/delete_wallet?walletName=' + walletNameForDelete.val().trim(), {method: 'POST'}).then(res => {
                if (res.ok) {
                    sessionStorage.clear();
                    Swal.fire({
                        title: 'Гаманець був успішно видалений.',
                        icon: 'success',
                    }).then(() => {
                        location.reload();
                    });
                } else if (res.status === 403) {
                    Swal.fire({
                        title: 'Помилка!',
                        text: 'Гаманець з назвою "Гаманець" не може бути видаленим.',
                        icon: 'error'
                    });
                } else {
                    Swal.fire({
                        title: 'Помилка!',
                        text: 'Введіть коректну назву гаманця.',
                        icon: 'error'
                    });
                }
            });
        }
    })
});

const deleteUserPurpose = (purposeName) => {
    return new Promise((resolve, reject) =>{
        fetch('/remove_purpose?purpose=' + purposeName, {method: 'POST'}).then(res =>{
            if (res.ok) {
                Toast.fire({
                    icon: 'success',
                    title: 'Запис видалено успішно.'
                })
resolve(true);
            }
        }).catch(() =>{
            reject(false);
        })
    });

}

const deleteButtonsForPurpose = document.querySelectorAll('.delete-purpose');

deleteButtonsForPurpose.forEach(function (button) {
    button.addEventListener('click', function () {
        const purpose = this.previousElementSibling;
        const purposeParentElement = purpose.parentElement;
        console.log(purposeParentElement);
        purposeParentElement.style.display = 'none';
       deleteUserPurpose(purpose.textContent).then(result =>{
           if (result){
               const purposeParentElement = purpose.parentElement;
               console.log(purposeParentElement);
               purposeParentElement.style.display = 'none';
           }
       })

    });
});


$('.transactions-for-all-time').on('click', () => {
    varPage = 0;
    getAllTransactionByWallet(varPage);
});
$('.show-more').on('click', () => {
    getAllTransactionByWallet(varPage);

});

const dropMenuButton = document.querySelector('.chose_wallet_menu');
const listOfWallets = document.querySelector('.list_of_wallets');
let checkForListOfWallets = 0;

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


