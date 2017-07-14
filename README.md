# FlowerAlmanac
Final project for UP ITDC Applications Development Program. 
This is the initial version of the app that implements data storage via a combination of Dropbox Core API and SQLite.

The Flower Almanac is a gardening tool app allowing users to add, view, edit and delete information on planting flowers.
Users may rate a flower's ease of planting and provide helpful tips on how to grow a specific flower.
Users may also choose to attach a photo of the flower taken by camera, or via gallery submission.
To save bandwidth, the app retrieves and uploads system-generated thumbnails of gallery photos in place of the actual file.

<div>
<h4>Dropbox oAuth - Login Page and Authorization</h4>
<img src="https://user-images.githubusercontent.com/29141643/28212376-bab30d3c-68d3-11e7-99fd-32c402de0b02.png" width="250">
<img src="https://user-images.githubusercontent.com/29141643/28212377-bafd266a-68d3-11e7-9800-7036211f169f.png" width="250">
</div>

<div>
<h4>App Main Page</h4>
<img src="https://user-images.githubusercontent.com/29141643/28212373-ba4c182a-68d3-11e7-8681-cdd4ccda0972.png" width="250">
<p>Successful login takes users to the main page, where they can perform add, view, edit and delete operations.</p>
<p>Whenever the database is empty, a message shows informing the user that nothing is stored in it.</p>
<p>Clicking on the purple button allows users to add new flowers to the list.</p>
</div>

<div>
<h4>Add Page</h4>
<img src="https://user-images.githubusercontent.com/29141643/28212380-bb786d20-68d3-11e7-9ce3-3e275851a6ad.png" width="250">
<img src="https://user-images.githubusercontent.com/29141643/28212381-bbb78a46-68d3-11e7-95a6-66b228bf6a4b.png" width="250">
<img src="https://user-images.githubusercontent.com/29141643/28212379-bb244470-68d3-11e7-8d50-5eb2438d8770.png" width="250">
<p>By default, flower items are assigned icons according to their ease of planting.</p>
<p>Users may choose to attach a photo to the flower item instead.</p>
</div>

<div>
<h4>Saving Photos</h4>
<img src="https://user-images.githubusercontent.com/29141643/28212378-bb13190c-68d3-11e7-811f-54ab226c625a.png" width="250">
<img src="https://user-images.githubusercontent.com/29141643/28212374-ba82aa52-68d3-11e7-9877-a4c10f467a9c.png" width="250">
<p>After saving, the default icon is initially displayed on the main page, while the photo is uploaded in the background.</p>
<p>Upon successful completion of upload, the list refreshes to reflect the photo. Adding, editing or deleting items on the list refreshes the main page.</p>
</div>








