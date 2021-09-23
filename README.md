# Курс основ программирования на МКН СПбГУ
# Проект 1: утилита diff

[Постановка задачи](./TASK.md)

### Запуск
Для удобной проверки проект скомпилирован в виде jar файла diff-kt.jar и запускается командой.
```sh 
java -Xss20m -jar diff-kt.jar [OPTIONS]
```
Приложение не имеет графической оболочки и запускается только так или в среде разработки. Всё поведение программы задаётся только переданными опциями.
##### Возможные проблемы с совместимостью
Программа тестировалась на Linux. В связи с этим цветной вывод может не работать или иметь другие цвета на вашей системе.
### Файлы для тестирования
Файлы для проверки работоспособности программы находятся в директории ``tests_files``. В качестве текста используется исходный код одного из модулей ядра Linux, с разностью во времени около 8 лет.

### Опции программы
Имена файлов передаются в качестве двух последних опций
Все опции программы на английском языке можно посмотреть при помощи встроенной справки.
Здесь будет чуть более подробное описание на русском.
+ ``-h`` или ``--help`` вызов встроенной справки
+ ``-f FILE`` или ``--file FILE`` перенаправляет вывод программы в файл
+ ``-w NUM`` или ``--width NUM`` устанавливает ширину столбца при выводе в режиме ``SPLIT``. Строчки длиннее этого числа, будут кончаться на `...`
+ ``-s SIGN`` или ``--sign SIGN`` Устанавливает один из режимов для подписи для различных блоков. Допустимыми значениями являются: `long`, `short`, `none`
+ ``-c MODE`` или ``--common MODE`` Устанавливает режим вывода для общей части файлов. Допустимыми значениями являются: `split`, `series`, `none`.  ``split`` выводит на одной строке части обоих файлов: слева часть первого переданного файла, а справа - правого. ``series`` выводит сначала весь отличающийся блок из одного файла, а затем из второго.
+ ``-d MODE`` или ``--diff MODE`` Устанавливает режим вывода для различной части файлов. Допустимые значения совпадают с ``--common``.
+ ``-o`` или ``--context`` Включает так называемый "контекстный режим". Опции ``--diff`` и ``--common`` влиять не будут. Основная идея режима - выводить не весь файл, а только различные части в контексте общей части.
+ ``-b NUM`` или ``--border NUM`` Устанавливает размер контекста для предыдущей опции.
### Коды возврата
Для удобного использования данной утилиты, в том числе, в скриптах-bash, коды возврата специфицированны следующим образом:
+ 0 - программа выполнилась успешно, отличий в файлах не найдено
+ 1 - программа выполнилась успешно, в файлах есть отличия
+ 2 - программа не смогла считать или записать файлы.
### Рекомендация по работе с большими файлами
При работе с файлами, сумма строк в которых превосходит 10000, необходимо использовать ``-Xss20m`` опцию JVM машины, для увеличения размера стека.
В противном случае стек будет переполняться из-за глубокой рекурсии.